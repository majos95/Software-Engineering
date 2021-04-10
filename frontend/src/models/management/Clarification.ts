export default class Clarification {
  author: string = '';
  description: string = '';

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.author = jsonObj.author;
      this.description = jsonObj.description;
    }
  }
}
