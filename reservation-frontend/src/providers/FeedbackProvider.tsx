// import { ToastContainer, toast } from 'react-toastify';
// import 'react-toastify/dist/ReactToastify.css';
//
// import React, { ReactNode } from 'react';
// import { FeedbackContext } from '../contexts/FeedbackContext';
//
// interface FeedbackProviderProps {
//     children: ReactNode;
// }
//
// export const FeedbackProvider: React.FC<FeedbackProviderProps> = ({ children }) => {
//     const showToast = (message: string, type: 'error' | 'success' | 'warn') => {
//         if (type === 'error') {
//             toast.error(message);
//         } else if (type === 'success') {
//             toast.success(message);
//         } else if (type === 'warn') {
//             toast.warn(message);
//         }
//     };
//
//     return (
//         <FeedbackContext.Provider value={{ showToast }}>
//             {children}
//             <ToastContainer />
//         </FeedbackContext.Provider>
//     );
// };
