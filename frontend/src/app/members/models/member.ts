export interface Member {
  id: number;
  name: string;
  email: string;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}

export interface MemberRequest {
  name: string;
  email: string;
}