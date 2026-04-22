export type NotepadEntry = {
  title: string,
  date: string,
  source: string,
  type: "tip" | "info" | "observation",
  content: string
}