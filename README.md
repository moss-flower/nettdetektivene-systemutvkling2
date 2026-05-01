# idatt2106_2026_02

```` møteplass
Oppmøterom: B22 (Berg-bygget)
Oppmøtetid: 09:00 - 15:00
````

### Medlemmer
- **Ole-Kristian Wigum**
- **Casper Kolsrud**
- **William Holtsdalen**
- **Hector Mendana Morales**
- **Alexander Tawan Atjanthuek Johnsen**
- **Amadeus Syvertsen Berg**
- **Wayan Satria Krabseth**
- **Sasha Unruh**

## Developer Setup

### Requirements

- Docker Desktop if you want to run the full stack with Docker
- Node.js or Bun if you want to run the frontend locally
- Java 21 if you want to run the backend locally

### Create `.env`

Create a `.env` file in the project root based on `.env.example`.

Example:

```env
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET_KEY=your_jwt_secret_key
NUXT_SESSION_PASSWORD=your_nuxt_session_password
```

### Run Everything With Docker Compose

This starts MySQL, backend, and frontend together.

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- MySQL: `localhost:3306`

Stop containers:

```bash
docker compose down
```

If the database volume needs to be removed as well, run:

```bash
docker compose down -v
```

If `down` is not enough, use `down` first and then `down -v` to fully remove the database volume.

After code changes, rebuild with:

```bash
docker compose up --build
```

### Run Frontend Locally

You can run the frontend without Docker using either `npm` or `bun`.

With `npm`:

```bash
cd frontend
npm install
npm run dev
```

With `bun`:

```bash
cd frontend
bun install
bun run dev
```

Frontend local URL:

- `http://localhost:3000`

### Run Backend Locally

```bash
cd backend
./mvnw spring-boot:run
```

The backend expects the database and other values from the root `.env`.

## Test Data

Backend seeds demo users and classrooms for local testing.

### Login

Shared password for all demo accounts: `test123`

Teacher accounts:
- `lisa.laerer@test.com` (`Lisa Lærer`)
- `per.laerer@test.com` (`Per Lærer`)

Student accounts:
- `ada.elev@test.com` (`Ada Elev`)
- `elias.elev@test.com` (`Elias Elev`)
- `sara.elev@test.com` (`Sara Elev`)
- `noah.elev@test.com` (`Noah Elev`)
- `ingrid.elev@test.com` (`Ingrid Elev`)
- `emil.elev@test.com` (`Emil Elev`)
- `mia.elev@test.com` (`Mia Elev`)
- `lukas.elev@test.com` (`Lukas Elev`)

Pending student accounts:
- `theo.venter@test.com` (`Theo Venter`)
- `amalie.venter@test.com` (`Amalie Venter`)
- `henrik.venter@test.com` (`Henrik Venter`)
- `nora.venter@test.com` (`Nora Venter`)
- `aksel.venter@test.com` (`Aksel Venter`)
- `vilde.venter@test.com` (`Vilde Venter`)

Student without classroom:
- `oda.utenklasse@test.com` (`Oda Uten Klasse`)

### Classrooms

- `3-trinn A` with code `SUPER-SKOG-KOPP`
- `3-trinn B` with code `MODIG-TAVLE-BRUS`
- `3-trinn C` with code `KONGE-LAVA-TRE`
- `7-trinn A` with code `TRYGG-REV-SOL`
- `7-trinn B` with code `KLAR-FJELL-BOK`
- `7-trinn C` with code `RASK-ELG-LYS`

### Current memberships

- `Ada Elev` and `Elias Elev` belong to `3-trinn A`; `Theo Venter` is pending
- `Sara Elev` belongs to `3-trinn B`; `Amalie Venter` is pending
- `Noah Elev` belongs to `3-trinn C`; `Henrik Venter` is pending
- `Ingrid Elev` and `Emil Elev` belong to `7-trinn A`; `Nora Venter` is pending
- `Mia Elev` belongs to `7-trinn B`; `Aksel Venter` is pending
- `Lukas Elev` belongs to `7-trinn C`; `Vilde Venter` is pending
- `Oda Uten Klasse` is not in a classroom yet

Teachers can log in directly to test teacher views. Active students can log in and test student flows without joining first. Pending students log in to the pending classroom page, and can be approved from the teacher classroom overview.

### Aktivitetsdiagram
![Aktivitetsdiagram](docs/flowchart.png)

### Spill modell code
![Spill modell code](docs/rootToTask-diagram.png)
