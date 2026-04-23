export type WeeklyMystery = {
  id: string;
  weekLabel: string;
  title: string;
  prompt: string;
  clue: string;
};

export type MysterySubmission = {
  id: string;
  submittedAt: string;
  message: string;
  screenshotName: string;
  status: 'Til vurdering';
};

const currentMystery: WeeklyMystery = {
  id: 'm-2026-17',
  weekLabel: 'Uke 17',
  title: 'Mysteriet om den ville overskriften',
  prompt: 'Finn et eksempel på en nyhetssak eller post som prøver å trigge sterke følelser.',
  clue: 'Se etter ord som vil gjøre deg redd, sint eller superstresset med en gang.',
};

const previousMysteries: WeeklyMystery[] = [
  {
    id: 'm-2026-16',
    weekLabel: 'Uke 16',
    title: 'Klikkagn eller ekte sak?',
    prompt: 'Sammenlign to overskrifter og finn hvilken som virker mest troverdig.',
    clue: 'Troverdige saker forklarer ofte hvem, hva og hvor uten overdrevne lovnader.',
  },
  {
    id: 'm-2026-15',
    weekLabel: 'Uke 15',
    title: 'Bildet som lurte alle',
    prompt: 'Finn detaljer i et bilde som kan avslore om det er redigert eller KI-laget.',
    clue: 'Sjekk hender, skygger, tekst og bakgrunn for rare detaljer.',
  },
  {
    id: 'm-2026-14',
    weekLabel: 'Uke 14',
    title: 'Hvem star bak avsenderen?',
    prompt: 'Vurder om en e-post eller melding faktisk kommer fra den den utgir seg for.',
    clue: 'Uvanlige domener og stavefeil i avsendernavn er et varseltegn.',
  },
];

export function useStudentMystery() {
  const submissionHistory = useState<MysterySubmission[]>('student-mystery-submissions', () => []);

  function submitMysteryExample(message: string, screenshotName: string) {
    const trimmedMessage = message.trim();
    const trimmedScreenshotName = screenshotName.trim();
    if (!trimmedMessage || !trimmedScreenshotName) {
      return;
    }

    submissionHistory.value.unshift({
      id: `submission-${Date.now()}`,
      submittedAt: new Date().toLocaleString('nb-NO', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      }),
      message: trimmedMessage,
      screenshotName: trimmedScreenshotName,
      status: 'Til vurdering',
    });
  }

  return {
    currentMystery,
    previousMysteries,
    submissionHistory,
    submitMysteryExample,
  };
}
