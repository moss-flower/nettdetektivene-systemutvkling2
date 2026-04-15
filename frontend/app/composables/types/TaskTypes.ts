// TaskTypes.ts
class TaskDTO {
    taskId: string = "";
    description: string = "";
}


// Task NewsStand; Which of these news articles are real and which are fake?
class TaskNewsStandDTO extends TaskDTO {
    correct: string[] = [];
    faulty: string[] = [];
}

// Task PostOffice; Which email is a scam and which is not?
class TaskPostOfficeDTO extends TaskDTO {
    tasks: TaskPostSingleton[] = [];
}

class TaskPostSingleton {
    imageurl: string = "";
    isGood: boolean = false;
}

// Task PasswordBank; Which passwords are weak and which are strong?
abstract class TaskPasswordBankDTO extends TaskDTO { }

class TaskPasswordBankGuessDTO extends TaskPasswordBankDTO {
    passwordWeak: string[] = [];
    passwordStrong: string[] = [];
}

class TaskPasswordBankImproveDTO extends TaskPasswordBankDTO {
    passwordStart: string = "";
}

class TaskPasswordBankMakePasswordDTO extends TaskPasswordBankDTO {
    password: string = "";
}

// Task ImageRecognition; Which images are real, AI-generated, or manipulated?
class TaskImageRecognitionDTO extends TaskDTO {
    imageurl: string = "";
    type: ImageRecognitionType = ImageRecognitionType.Real;
}


enum ImageRecognitionType {
    Real,
    AI,
    Manipulated
}

// Task Marketplace; Which products should be bought, reviewed more, or not bought?
class TaskMarketplaceDTO extends TaskDTO {
    tasks: TaskMarketplaceSingleton[] = [];
}

class TaskMarketplaceSingleton {
    imageurl: string = "";
    type: MarketplaceChoices = MarketplaceChoices.Buy;
}

enum MarketplaceChoices {
    Buy,
    ReviewMore,
    DontBuy
}

// Task SocialMedia; What to do with a social media post: share, hold, check sources, or ask an adult?
class TaskSocialMediaDTO extends TaskDTO {
    imageurl: string = "";
    choice: SocialMediaChoices = SocialMediaChoices.Share;
}

enum SocialMediaChoices {
    Share,
    Hold,
    CheckSources,
    AskAdult
}

class TaskFinalBossDTO extends TaskDTO {
    tasks: TaskDTO[] = [];
}

// Exporting all the classes and enums
export {
    TaskDTO,
    TaskNewsStandDTO,
    TaskPostOfficeDTO,
    TaskPostSingleton,
    TaskPasswordBankDTO,
    TaskPasswordBankGuessDTO,
    TaskPasswordBankImproveDTO,
    TaskPasswordBankMakePasswordDTO,
    TaskImageRecognitionDTO,
    ImageRecognitionType,
    TaskMarketplaceDTO,
    TaskMarketplaceSingleton,
    MarketplaceChoices,
    TaskSocialMediaDTO,
    SocialMediaChoices,
    TaskFinalBossDTO
};