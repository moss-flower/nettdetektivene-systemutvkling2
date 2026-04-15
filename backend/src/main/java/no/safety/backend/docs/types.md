- Types

  - Spiller:
    - Navn<string>
    - HashPassword+salt<string>
    - CompletedTasks<list, string>
    - ClassromId<string>
    - AvatarCondig<AvatarData>

  - AvatarData : DataStruktur
    - Hair<int_id>
    - HairColor<string_hex>
    - EyeColor<string_hex>
    - Hat<int_id>
    - Hair<int_id>
    - Clothes<int_id>

  - Lærer
    - Navn<string>
    - HashPassword+salt<string>
    - ClassromIds<list : string>

  - AvatarHair
    - key: int
    - value: imageurl

  - AvatarClothing
    - key: int
    - value: imageurl

  - AvatarHat
    - key: int
    - value: imageurl

UploadThing.com : for å laste opp og lagre bilder av avatar-deler. Vi kan bruke dette til å lagre bilder av hår, klær og hatter, og deretter hente dem når vi trenger å vise avatarene i appen.