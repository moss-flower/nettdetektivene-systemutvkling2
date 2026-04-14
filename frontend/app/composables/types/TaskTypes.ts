// TaskTypes.ts
class TaskDTO {
    taskId: string = "";
    description: string = "";
    taskAmount: number = 4;
}


// Task NewsStand; Which of these news articles are real and which are fake?
class TaskNewsStandDTO extends TaskDTO {
    articles: TaskNewsStandSingleton[] = [];
}

class TaskNewsStandSingleton {
    correct: NewsArticle = new NewsArticle();
    fake: NewsArticle = new NewsArticle();
}

class NewsArticle {
    author: string = "";
    title: string = ""
    imageurl: string = "";
    text: string = "";
}

// Task PostOffice; Which email is a scam and which is not?
class TaskPostOfficeDTO extends TaskDTO {
    mails: TaskPostSingleton[] = [];
}

class TaskPostSingleton {
    avatarImageurl: string = "";
    sender: string = "";
    subject: string = "";
    content: string = "";
    isValid: boolean = false;
}

// Task PasswordBank; Which passwords are weak and which are strong?
abstract class TaskPasswordBankSingletonAbstract extends TaskDTO { }

class TaskPasswordBankGuessSingleton extends TaskPasswordBankSingletonAbstract {
    passwordWeak: string[] = [];
    passwordStrong: string[] = [];
}

class TaskPasswordBankImproveSingleton extends TaskPasswordBankSingletonAbstract {
    passwordStart: string = "";
}

class TaskPasswordBankMakePasswordSingleton extends TaskPasswordBankSingletonAbstract {
    password: string = "";
}

class TaskPasswordBankDTO extends TaskDTO {
    passwordTasks: TaskPasswordBankSingletonAbstract[] = [];
}

// Task ImageRecognition; Which images are real, AI-generated, or manipulated?
class TaskImageRecognitionDTO extends TaskDTO {
    images: TaskImageRecognitionSingleton[] = [];
}

class TaskImageRecognitionSingleton {
    imageurl: string = ""
    type: ImageRecognitionType = ImageRecognitionType.Real;
}

enum ImageRecognitionType {
    Real,
    AI,
    Manipulated
}

// Task Marketplace; Which products should be bought, reviewed more, or not bought?
class TaskMarketplaceDTO extends TaskDTO {
    listing: TaskMarketplaceSingleton[] = [];
}

class TaskMarketplaceSingleton {
    imageurl: string = "";
    price: number = 0;
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
    TaskNewsStandSingleton,
    TaskPostOfficeDTO,
    TaskPostSingleton,
    TaskPasswordBankSingletonAbstract,
    TaskPasswordBankGuessSingleton,
    TaskPasswordBankImproveSingleton,
    TaskPasswordBankMakePasswordSingleton,
    TaskPasswordBankDTO,
    TaskImageRecognitionDTO,
    TaskImageRecognitionSingleton,
    ImageRecognitionType,
    TaskMarketplaceDTO,
    TaskMarketplaceSingleton,
    MarketplaceChoices,
    TaskSocialMediaDTO,
    SocialMediaChoices,
    TaskFinalBossDTO
};