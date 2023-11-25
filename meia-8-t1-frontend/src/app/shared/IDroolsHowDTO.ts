export interface IDroolsHowDTO {
    rule: string;
    lhs: string;
    conclusion: Conclusion;
  }

interface Conclusion{
  description: string,
  probability?: number
}
