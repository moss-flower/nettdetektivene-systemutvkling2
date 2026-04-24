# idatt2106_2026_02

```` møteplass
Oppmøterom: B22 (Berg-bygget)
Oppmøtetid: 09:00 - 15:00
````

### Medlemmer
- **Ole-Kristian Wigum**
- **Casper Kolsrud**
- **William Holtsdalen**

## Run Project

The project is set up to be easily run using Docker Compose. 

#### Follow these steps:
1. Fill out a .env placed at project root. Make sure it follows the specifications in .env.example
2. Make sure the Docker application is running
3. Navigate your terminal to the project root
4. Run: 
``` 
docker compose up
```

If you eventually implement changes:
``` 
docker compose down
docker compose up --build
```

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

### Classrooms

- `Klasse 5B` with code `SUPER-SKOG-KOPP`
- `Klasse 6A` with code `MODIG-TAVLE-BRUS`
- `Nettdetektivene` with code `KONGE-LAVA-TRE`

### Current memberships

- `Ada Elev`, `Elias Elev` and `Sara Elev` belong to `Klasse 5B`
- `Noah Elev` and `Ingrid Elev` belong to `Klasse 6A`
- `Emil Elev` belongs to `Nettdetektivene`
- `Mia Elev` and `Lukas Elev` are not in a classroom yet

Teachers can log in directly to test teacher views. Students already assigned to a classroom can log in and test student flows without joining first. Use `Mia Elev` or `Lukas Elev` together with the join codes above to test classroom join flows.

### Aktivitetsdiagram
![Aktivitetsdiagram](docs/flowchart.png)

### Spill modell code
![Spill modell code](docs/rootToTask-diagram.png)

## Factory Routes

Factory-sidene i frontend støtter nå direkte åpning av en spesifikk ressurs via `?uid=` i URL-en.

Når `uid` er satt:
- riktig world, station eller exercise åpnes direkte i editoren
- overskrift og nedtrekksliste for valg skjules
- brukeren låses til ressursen i URL-en og kan ikke velge en annen fra siden

Eksempler:
- ` /teacher/factory/worldFactory?uid=min-verden `
- ` /teacher/factory/stationFactory?uid=min-stasjon `
- ` /teacher/factory/exerciseFactory?uid=min-oppgave `
