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

export type SortBucket = {
  key: string;
  title: string;
  description?: string;
  backgroundColor?: string;
};

export type SortableItem = {
  id: string;
  label: string;
  correctBucketKey: string;
  explanation?: string;
};

export type SortIntoBucketsResult = {
  correct: boolean;
  placements: Record<string, string[]>;
};

export type PasswordStrengthPromptContent = {
  title?: string;
  text?: string;
  helperText?: string;
};

export type PasswordStrengthResult = {
  correct: boolean;
  password: string;
  score: number;
  missingStartingPassword?: boolean;
};

export type ExerciseTemplateKind =
  | "x-or-y"
  | "multiple-choice"
  | "email-hotspots"
  | "sort-into-buckets"
  | "password-strength";

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

export type SortIntoBucketsTaskData = TaskBase & {
  templateKind: "sort-into-buckets";
  prompt?: {
    title?: string;
    text?: string;
  };
  unplacedLabel?: string;
  buckets: SortBucket[];
  items: SortableItem[];
};

export type PasswordStrengthTaskData = TaskBase & {
  templateKind: "password-strength";
  prompt?: PasswordStrengthPromptContent;
  mode: "create" | "improve";
  placeholder?: string;
  startingPassword?: string;
  minLength?: number;
  minScoreToPass: number;
  commonPatterns?: string[];
};

export type ExerciseTask =
  | XOrYTaskData
  | MultipleChoiceTaskData
  | EmailHotspotTaskData
  | SortIntoBucketsTaskData
  | PasswordStrengthTaskData;
