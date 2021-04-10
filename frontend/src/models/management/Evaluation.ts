import Question from '@/models/management/Question';

export default class Evaluation {
  approvedEvaluation: boolean | null = null;
  id: number | null = null;
  justification: string = '';
  teacherUsername: string | null = null;
  submittedQuestion: Question | null = null;

  constructor(jsonObj?: Evaluation) {
    if(jsonObj){
      this.id = jsonObj.id;
      this.approvedEvaluation = jsonObj.approvedEvaluation;
      this.justification = jsonObj.justification;
      this.teacherUsername = jsonObj.teacherUsername;
      this.submittedQuestion = jsonObj.submittedQuestion;
    }
  }
}