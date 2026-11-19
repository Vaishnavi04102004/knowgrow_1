# KnowGrow — API + Demo frontend

React + TypeScript + Vite frontend for the Organisational Knowledge Gap Intelligence Platform.

## Run in VS Code

1. Extract the ZIP and open the `knowgrow-frontend` folder.
2. Run `npm install`.
3. Run `npm run dev`.
4. Open the local URL shown by Vite (normally `http://localhost:5173`).

## Demo workspace

On the sign-in screen choose **Try Demo Workspace**. Demo mode is stored in this browser and is clearly marked. You can:

- Sign in using one of the sample accounts below.
- Register your own demo account with your own email and password. You are signed in after registration.
- During demo registration, choose Employee, Manager, Mentor, HR Specialist or Department Head to demonstrate each role's view.
- Sign in as HR Specialist and open **Employee Directory** to add employees or edit their role, department, team and job title.
- Switch demo roles by signing out and signing in with another demo account.

Sample accounts (demo password for all: `demo12345`):

| Role | Email |
|---|---|
| HR Specialist | hr@knowgrow.demo |
| Manager / Team Lead | manager@knowgrow.demo |
| Employee | employee@knowgrow.demo |
| Mentor | mentor@knowgrow.demo |
| Department Head | head@knowgrow.demo |

Demo accounts and employee records are stored in this browser's local storage only. They are synthetic demonstration data, not a real organisation database. Demo passwords are not secure and must never be used for real accounts. Clearing browser storage resets the demo workspace.

## Live backend mode

Choose **Switch to Live Backend Login** on the sign-in screen to use the Java Spring Boot API adapter. Configure `.env` with:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

Restart Vite after changing environment variables. The backend must implement the endpoints in `docs/API_CONTRACT.md`. Real registration, Google OAuth and phone OTP require backend OAuth/SMS configuration. Public users must not be allowed to self-assign privileged organisation roles; the demo role selector is only for presentations.

## Important boundary

The demo mode exists only to demonstrate the user journey and role-specific UI. It does not provide production authentication, secure password storage, shared multi-user data, real OTP, Google OAuth, or backend authorization. The backend must enforce identity, roles, permissions and record access.

## Demo sign-in and role preview

Open **Try Demo Workspace** on the sign-in screen to reveal five role shortcuts: Employee, Team Lead / Manager, Mentor, HR Specialist and Department Head. Select a role to enter its seeded synthetic demo workspace without typing sample email addresses or passwords. The sign-in and registration screens retain the original KnowGrow purple visual design. In Demo mode, email registration also exposes role, department and team fields for creating synthetic demo participants; production registration does not grant privileged roles.
