# Build retrospective

## What went well
- Keeping services behind DAO interfaces made the marketplace rules testable independently of JDBC.
- The checkout transaction applies stock changes, captures purchase prices, writes order lines, and clears the cart as one unit.
- The WAR remains deployable to external Tomcat 9; the embedded runner is an opt-in local-development profile.

## Follow-ups
- Replace the mock payment strategy before accepting real payments.
- Add CSRF protection, email verification, production secrets, and operational monitoring before a public production launch.
- Add migrations through an established migration runner before changing a live database schema.
