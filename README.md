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
