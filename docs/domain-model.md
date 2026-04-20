# Domain Model

This diagram reflects the current code as implemented.

- `Classroom` is a backend entity.
- `World`, `Station`, `Exercise`, and `Task` are currently frontend content models.
- A classroom points to one world through `worldUid`.
- The content chain uses UID references, not nested persistence relations:
  - `World.stationUids: string[]`
  - `Station.exerciseUids: string[]`

```mermaid
classDiagram
    class Classroom {
        Long id
        String code
        String name
        String description
        String worldUid
        Set~User~ owners
        Set~StudentUser~ members
    }

    class User {
        UUID publicUuid
        String username
    }

    class StudentUser {
        Classroom classroom
    }

    class World {
        String uid
        String description
        String image
        Array~String~ stationUids
    }

    class Station {
        String uid
        String title
        String description
        String image
        Array~String~ exerciseUids
    }

    class Exercise {
        String uid
        String title
        String entryName
        String description
        Array~ExerciseTask~ tasks
    }

    class TaskBase {
        String id
        String templateKind
        String introText
        String feedbackCorrect
        String feedbackIncorrect
    }

    class XOrYTaskData {
        Prompt prompt
        XOrYPair options
        String correctChoice
    }

    class MultipleChoiceTaskData {
        MultipleChoicePromptContent prompt
        Array~MultipleChoiceOptionContent~ options
        String correctChoice
    }

    class EmailHotspotTaskData {
        Prompt prompt
        EmailHotspotContent email
        Array~EmailHotspotTarget~ targets
    }

    Classroom --> User : owners
    Classroom --> StudentUser : members
    Classroom --> World : worldUid resolves to
    StudentUser --> Classroom : classroom

    World --> Station : stationUids[] resolves to
    Station --> Exercise : exerciseUids[] resolves to
    Exercise --> TaskBase : tasks[]

    TaskBase <|-- XOrYTaskData
    TaskBase <|-- MultipleChoiceTaskData
    TaskBase <|-- EmailHotspotTaskData
```

## Notes

- `Classroom` stores one `worldUid`, which links a classroom to a single world.
- `World` does not currently have a `title`; it uses `uid`, `description`, `image`, and `stationUids`.
- `World -> Station` and `Station -> Exercise` are resolved through UID arrays in the frontend models.
