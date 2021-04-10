import Clarification from '@/models/management/Clarification';
import Discussion from '@/models/management/Discussion';

export default class Doubt {
  id: number | null = null;
  author: string = '';
  status: string = 'UNSOLVED';
  content: string = '';
  clarificationDto: Clarification | null = null;
  creationDate!: String | null;
  isNew: Boolean = false;
  doubtType: string = 'PRINCIPAL';
  showDoubt: boolean = false;
  questionTitle: string = '';

  constructor(jsonObj?: Doubt) {
    if (jsonObj) {
      this.creationDate = jsonObj.creationDate;
      this.isNew = jsonObj.isNew;
      this.id = jsonObj.id;
      this.author = jsonObj.author;
      this.status = jsonObj.status;
      this.content = jsonObj.content;
      this.clarificationDto = jsonObj.clarificationDto;
      this.questionTitle = jsonObj.questionTitle;
      this.doubtType = jsonObj.doubtType;
    }
  }
}
