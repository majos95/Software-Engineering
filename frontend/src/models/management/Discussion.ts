import Doubt from '@/models/management/Doubt';

export default class Discussion {
  postsDto: Doubt[] = [];
  id: number | null = null;
  visibility: String = 'PRIVATE';
  questionTitle: string = '';
  title: string = '';
  author: string = '';
  authorId: number | null = null;
  status: String = 'OPEN';

  constructor(jsonObj?: Discussion) {
    if (jsonObj) {
      this.title = jsonObj.title;
      this.id = jsonObj.id;
      this.visibility = jsonObj.visibility;
      this.authorId = jsonObj.authorId;
      this.questionTitle = jsonObj.questionTitle;
      this.postsDto = jsonObj.postsDto.map((doubt: Doubt) => new Doubt(doubt));
      // @ts-ignore
      this.postsDto.sort((a, b) => a.id - b.id);
      this.author = jsonObj.author;
      this.status = jsonObj.status;
    }
  }
}
