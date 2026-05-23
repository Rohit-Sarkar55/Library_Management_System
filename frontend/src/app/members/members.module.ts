import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MembersRoutingModule } from './members-routing.module';
import { MemberListComponent } from './components/member-list/member-list.component';
import { MemberFormComponent } from './components/member-form/member-form.component';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MembersRoutingModule,
    MemberListComponent,
    MemberFormComponent
  ]
})
export class MembersModule {}