# 🪙 BharatCrypto | Next-Gen Cryptocurrency Trading Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue)](https://reactjs.org/)
[![Security](https://img.shields.io/badge/Spring%20Security-JWT-red)](https://spring.io/projects/spring-security)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind-CSS-3.0-blueviolet)](https://tailwindcss.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> **"Empowering the future of decentralized finance with institutional-grade security and real-time trading analytics."**

**BharatCrypto** is a comprehensive, full-stack cryptocurrency trading ecosystem. Built for scalability and security, it bridges the gap between complex blockchain technologies and user-friendly financial interfaces. Whether you are a trader managing a portfolio or an admin overseeing withdrawals, BharatCrypto provides a seamless experience.

---

## 🚀 Key Features

### 🔐 Advanced Security & Authentication
*   **JWT-Based Stateless Authentication**: Secure, scalable session management using `HS384` algorithm signatures (`JwtProvider.java`).
*   **Two-Factor Authentication (2FA)**: Integrated directly into the user model for enhanced account protection.
*   **Role-Based Access Control (RBAC)**: Distinct portals for `ROLE_USER` (Traders) and `ROLE_ADMIN` (Platform Managers).

### 💹 Trading & Portfolio Management
*   **Real-Time Watchlist**: Users can track their favorite assets with live updates.
*   **Asset Management**: Comprehensive dashboard to view holdings, transaction history, and account status.
*   **Withdrawal System**: Secure withdrawal request flow with Admin approval/rejection capabilities.

### 🛠️ Admin Dashboard
*   **Withdrawal Oversight**: Admins can view, approve, or decline withdrawal requests with a single click (`WithdrawalAdmin.jsx`).
*   **User Verification**: Monitor KYC status (`PENDING` vs `VERIFIED`) and user details.

---

## 🏗️ Technical Architecture

BharatCrypto follows a modern **Microservices-ready** architecture, separating the frontend and backend concerns for maximum scalability.

### 🔌 Backend (The Core)
*   **Framework**: **Spring Boot** (Java) - The backbone of the application.
*   **Security**: **Spring Security** with custom `JwtTokenValidator` filters.
*   **Database**: **JPA / Hibernate** (ORM) for efficient data persistence (MySQL/PostgreSQL).
*   **API**: RESTful endpoints exposing resources for Users, Wallets, and Admin operations.
*   **Build Tool**: Maven.

### 💻 Frontend (The Interface)
*   **Framework**: **React.js** - Component-based UI architecture.
*   **State Management**: **Redux Toolkit** - Centralized state for Auth, Withdrawal, and Asset data.
*   **Styling**: **Tailwind CSS** & **Shadcn UI** - For a responsive, accessible, and beautiful design.
*   **HTTP Client**: **Axios** - Configured with interceptors for automatic Token injection and error handling (`api.js`).

---

## 📂 Project Structure

```bash
BharatCrypto/
├── Backend-Spring boot/       # Java Spring Boot Server
│   ├── src/main/java/com/BharatCrypto/
│   │   ├── config/            # JWT Providers & Security Config
│   │   ├── model/             # JPA Entities (User, Watchlist, etc.)
│   │   ├── domain/            # Enums (USER_ROLE, UserStatus)
│   │   └── ...
│   └── ...
├── Frontend-React/            # React Client Application
│   ├── src/
│   │   ├── Admin/             # Admin Dashboard Components
│   │   ├── Api/               # Axios Configuration (api.js)
│   │   ├── Redux/             # State Management Slices
│   │   └── ...
│   └── ...
└── README.md                  # You are here!
```

---

## ⚡ Getting Started

### Prerequisites
*   **Java JDK 17+**
*   **Node.js v16+**
*   **MySQL** (or compatible SQL database)

### 1. Backend Setup
Navigate to the backend directory and start the Spring Boot server.

```bash
cd "Backend-Spring boot"
# Ensure application.properties is configured with your DB and JWT Secret
./mvnw spring-boot:run
```

### 2. Frontend Setup
Install dependencies and launch the React development server.

```bash
cd "Frontend-React"
npm install
npm run dev
```

The application will be available at `http://localhost:5173` (Frontend) and `http://localhost:5454` (Backend API).

---

## 🛡️ Security Highlights

*   **Token Validation**: Custom `JwtTokenValidator` ensures every request to protected routes (`/api/**`) carries a valid Bearer token.
*   **Data Sanitization**: Frontend interceptors (`api.js`) automatically clean malformed tokens to prevent 403/500 errors.
*   **Sensitive Data**: Passwords are never stored in plain text; `JsonProperty.Access.WRITE_ONLY` ensures they don't leak in API responses.

---

## 🤝 Contributing

We welcome contributions! Please fork the repository and submit a Pull Request.

---

*Built with ❤️ by the BharatCrypto Team*