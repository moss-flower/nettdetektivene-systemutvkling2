type BaseTask = {
  id: string;
  kind:
    | "newsStand"
    | "postOffice"
    | "passwordBank"
    | "imageRecognition"
    | "marketplace"
    | "socialMedia"
    | "finalBoss";
  description: string;
};

export type ImageRecognitionType = "real" | "AI" | "manipulated";
export type MarketplaceChoice = "buy" | "reviewMore" | "dontBuy";
export type SocialMediaChoice = "share" | "hold" | "checkSources" | "askAdult";

export type NewsArticle = {
  author: string;
  title: string;
  imageUrl: string;
  text: string;
};

export type NewsStandPair = {
  correct: NewsArticle;
  fake: NewsArticle;
};

export type NewsStandTask = BaseTask & {
  kind: "newsStand";
  articles: NewsStandPair[];
};

export type PostOfficeMail = {
  avatarImageUrl: string;
  sender: string;
  subject: string;
  content: string;
  isValid: boolean;
};

export type PostOfficeTask = BaseTask & {
  kind: "postOffice";
  mails: PostOfficeMail[];
};

export type PasswordBankItem = {
  taskId: string,
  guide: string,
  task: {
      kind: "guess";
      weakPasswords: string[];
      strongPasswords: string[];
    }
  | {
      kind: "improve";
      passwordStart: string;
    }
  | {
      kind: "makePassword";
      inputPassword: string;
    };
  }

export type PasswordBankTask = BaseTask & {
  kind: "passwordBank";
  items: PasswordBankItem[];
};

export type ImageRecognitionItem = {
  imageUrl: string;
  type: ImageRecognitionType;
};

export type ImageRecognitionTask = BaseTask & {
  kind: "imageRecognition";
  images: ImageRecognitionItem[];
};

export type MarketplaceListing = {
  imageUrl: string;
  price: number;
  expectedChoice: MarketplaceChoice;
};

export type MarketplaceTask = BaseTask & {
  kind: "marketplace";
  listings: MarketplaceListing[];
};

export type SocialMediaPost = {
  postId: string;
  imageUrl: string;
  expectedChoice: SocialMediaChoice;
};

export type SocialMediaTask = BaseTask & {
  kind: "socialMedia";
  posts: SocialMediaPost[];
};

export type NonFinalTask =
  | NewsStandTask
  | PostOfficeTask
  | PasswordBankTask
  | ImageRecognitionTask
  | MarketplaceTask
  | SocialMediaTask;

export type FinalBossTask = BaseTask & {
  kind: "finalBoss";
  tasks: NonFinalTask[];
};
