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
export type XOrYChoice = "x" | "y";
export type XOrYPair<T> = {
  x: T;
  y: T;
};
export type XOrYOptionContent = {
  imageUrl: string;
  imageAlt: string;
  eyebrow?: string;
  title: string;
  text: string;
};
export type XOrYResult = {
  correct: boolean;
  choice: XOrYChoice;
};
export type MultipleChoiceOptionKey = string;
export type MultipleChoicePromptContent = {
  imageUrl: string;
  imageAlt: string;
  title?: string;
  text?: string;
};
export type MultipleChoiceOptionContent = {
  key: MultipleChoiceOptionKey;
  title: string;
};
export type MultipleChoiceResult = {
  correct: boolean;
  choice: MultipleChoiceOptionKey;
};
export type EmailHotspotTarget = {
  id: string;
  isSuspicious: boolean;
};
export type EmailHotspotBodyTextBlock = {
  id: string;
  type: "text";
  text: string;
  targetId?: string;
};
export type EmailHotspotBodyLinkBlock = {
  id: string;
  type: "link";
  label: string;
  urlText: string;
  helperText?: string;
  targetId?: string;
};
export type EmailHotspotBodyBlock =
  | EmailHotspotBodyTextBlock
  | EmailHotspotBodyLinkBlock;
export type EmailHotspotAttachment = {
  id: string;
  name: string;
  details?: string;
  targetId?: string;
};
export type EmailHotspotActionButton = {
  label: string;
  helperText?: string;
  targetId?: string;
};
export type EmailHotspotContent = {
  senderName: string;
  senderNameTargetId?: string;
  senderAddress: string;
  senderAddressTargetId?: string;
  subject: string;
  subjectTargetId?: string;
  senderAvatarText?: string;
  receivedLabel?: string;
  receivedLabelTargetId?: string;
  greeting?: string;
  greetingTargetId?: string;
  bodyBlocks: EmailHotspotBodyBlock[];
  actionButton?: EmailHotspotActionButton;
  attachments?: EmailHotspotAttachment[];
  closing?: string;
  closingTargetId?: string;
};
export type EmailHotspotResult = {
  correct: boolean;
  selectedTargetIds: string[];
  incorrectTargetId?: string;
};

export type ExerciseTemplateKind = "x-or-y" | "multiple-choice" | "email-hotspots";

export type TaskBase = {
  id: string;
  templateKind: ExerciseTemplateKind;
  introText: string;
  feedbackCorrect: string;
  feedbackIncorrect: string;
};

export type XOrYTaskData = TaskBase & {
  templateKind: "x-or-y";
  prompt?: {
    title?: string;
    text?: string;
  };
  options: XOrYPair<XOrYOptionContent>;
  correctChoice: XOrYChoice;
};

export type MultipleChoiceTaskData = TaskBase & {
  templateKind: "multiple-choice";
  prompt: MultipleChoicePromptContent;
  options: MultipleChoiceOptionContent[];
  correctChoice: MultipleChoiceOptionKey;
};

export type EmailHotspotTaskData = TaskBase & {
  templateKind: "email-hotspots";
  prompt?: {
    title?: string;
    text?: string;
  };
  email: EmailHotspotContent;
  targets: EmailHotspotTarget[];
};

export type ExerciseTask =
  | XOrYTaskData
  | MultipleChoiceTaskData
  | EmailHotspotTaskData;

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
  | PostOfficeTask
  | PasswordBankTask
  | ImageRecognitionTask
  | MarketplaceTask
  | SocialMediaTask;

export type FinalBossTask = BaseTask & {
  kind: "finalBoss";
  tasks: NonFinalTask[];
};
