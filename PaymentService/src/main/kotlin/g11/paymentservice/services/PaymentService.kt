package g11.paymentservice.services

import com.paypal.sdk.PaypalServerSdkClient
import com.paypal.sdk.models.*
import g11.paymentservice.configs.PayPalProperties
import g11.paymentservice.dtos.*
import g11.paymentservice.entities.OutboxEvent
import g11.paymentservice.entities.Payment
import g11.paymentservice.entities.enumerations.PaymentStatus
import g11.paymentservice.repositories.OutboxEventRepository
import g11.paymentservice.repositories.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class PaymentService(
    private val payPalClient: PaypalServerSdkClient,
    private val paymentRepository: PaymentRepository,
    private val outboxEventRepository: OutboxEventRepository,
    private val paymentEventPublisher: PaymentEventPublisher,
    private val payPalProperties: PayPalProperties
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(PaymentService::class.java)

    fun initiatePayment(request: PaymentInitiationRequest): PaymentInitiationResponse {
        logger.info("Initiating PayPal payment for reservationId=${request.reservationId}")

        val orderRequest = OrderRequest().apply {
            intent = CheckoutPaymentIntent.CAPTURE
            purchaseUnits = listOf(
                PurchaseUnitRequest().apply {
                    amount = AmountWithBreakdown().apply {
                        currencyCode = request.currency
                        value = String.format("%.2f", request.amount)
                    }
                }
            )
            applicationContext = OrderApplicationContext().apply {
                returnUrl = payPalProperties.returnUrl
                cancelUrl = payPalProperties.cancelUrl
            }
        }

        val input = CreateOrderInput().apply { body = orderRequest }

        val response = try {
            payPalClient.ordersController.createOrder(input)
        } catch (e: Exception) {
            logger.error("PayPal order creation failed", e)
            throw IllegalStateException("Failed to initiate payment with PayPal", e)
        }

        val approvalUrl = response.result?.links?.firstOrNull { it.rel == "approve" }?.href
            ?: throw IllegalStateException("No approval URL returned by PayPal")

        val transactionId = response.result?.id
            ?: throw IllegalStateException("No transaction ID returned by PayPal")

        val payment = Payment(
            id = 0L,
            reservationId = request.reservationId,
            userId = request.userId,
            amount = request.amount,
            currency = request.currency,
            status = PaymentStatus.INITIATED,
            paypalTransactionId = transactionId,
            updatedAt = LocalDateTime.now()
        )
        val savedPayment = paymentRepository.save(payment)

        logger.info("Approval URL: $approvalUrl")

        return PaymentInitiationResponse(savedPayment.id, approvalUrl)
    }

    @Transactional
    fun confirmPayment(request: PaymentConfirmationRequest): PaymentConfirmationResponse {
        return try {
            val response = payPalClient.ordersController.captureOrder(CaptureOrderInput().apply { id = request.orderId })

            if (response.statusCode != 201) {
                logger.error("PayPal capture failed with status ${response.statusCode}")
                return PaymentConfirmationResponse(false, "PayPal capture failed with status ${response.statusCode}")
            }

            val payment = paymentRepository.findByPaypalTransactionId(request.orderId)
                ?: return PaymentConfirmationResponse(false, "Payment not found for order ID ${request.orderId}")

            payment.status = PaymentStatus.SUCCESS
            payment.updatedAt = LocalDateTime.now()
            paymentRepository.save(payment)

            val paymentEvent = PaymentEvent(
                reservationId = payment.reservationId,
                userId = payment.userId,
                amount = payment.amount,
                status = payment.status
            )
            paymentEventPublisher.publishPaymentCompletedEvent(paymentEvent, payment.reservationId.toString())

            // Save the outbox event with proper details
            val outboxEvent = OutboxEvent(
                aggregateId = payment.reservationId.toString(),
                aggregateType = "Payment",
                eventType = "PAYMENT_COMPLETED",
                createdAt = LocalDateTime.now(),
            )
            logger.info("Saving outbox event for reservation ID ${payment.reservationId}")
            outboxEventRepository.save(outboxEvent)

            logger.info("Payment completed and event published for order ID ${request.orderId}")

            return PaymentConfirmationResponse(true, "Payment confirmed successfully", payment.reservationId)
        } catch (ex: Exception) {
            logger.error("Error occurred during payment confirmation for order ID ${request.orderId}", ex)
            return PaymentConfirmationResponse(false, "Unexpected error occurred: ${ex.message ?: "No details available"}")
        }
    }

}

