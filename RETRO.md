# Build Retrospective

## What went well
- Keeping services behind DAO interfaces made marketplace rules testable independently of JDBC.
- Atomic checkout in `JdbcOrderDao` reliably coordinates stock decrement, purchase price capture, and cart clearance.
- Pluggable chatbot architecture allows seamless swapping between Google Gemini API and deterministic offline FAQ responses.
- Centralized `AuthFilter` and `LoggingFilter` keep controllers focused strictly on HTTP coordination.

## What could be improved
- Commit activity was sparse during initial weeks due to architectural planning and local experimentation before steady commits resumed.
- Embedded H2 file mode works well for demonstration but should be migrated to PostgreSQL/MySQL for multi-node deployments.
- Payment processing remains simulated via `MockPaymentStrategy` and needs a commercial gateway (Razorpay/Stripe) for production.
- CSRF tokens and automated database migration tooling should be introduced before public production rollout.
