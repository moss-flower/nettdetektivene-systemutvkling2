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

//================= NewsStand =================

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


//================= PostOffice =================

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


//================= PasswordBank =================

export type PasswordBankGuessItem = {
  itemId: string;
  kind: "guess";
  guide: string;
  weakPasswords: string[];
  strongPasswords: string[];
};

export type PasswordBankMakePasswordItem = {
  itemId: string;
  kind: "makePassword";
  guide: string;
  inputPassword: string;
};

export type PasswordBankImprovePasswordItem = {
  itemId: string;
  kind: "improvePassword";
  guide: string;
  passwordStart: string;
};

export type PasswordBankExercise =
  | PasswordBankGuessItem
  | PasswordBankMakePasswordItem
  | PasswordBankImprovePasswordItem;

export type PasswordBankTask = BaseTask & {
  kind: "passwordBank";
  items: PasswordBankExercise[];
};


//================= ImageRecognition =================

export type ImageRecognitionItem = {
  imageUrl: string;
  type: ImageRecognitionType;
};

export type ImageRecognitionTask = BaseTask & {
  kind: "imageRecognition";
  images: ImageRecognitionItem[];
};

//================= Marketplace =================

export type MarketplaceListing = {
  imageUrl: string;
  price: number;
  expectedChoice: MarketplaceChoice;
};

export type MarketplaceTask = BaseTask & {
  kind: "marketplace";
  listings: MarketplaceListing[];
};

//================= SocialMedia =================

export type SocialMediaPost = {
  postId: string;
  imageUrl: string;
  expectedChoice: SocialMediaChoice;
};

export type SocialMediaTask = BaseTask & {
  kind: "socialMedia";
  posts: SocialMediaPost[];
};

//================= Boss =================
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
