import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ViewRequestHistoryComponent } from './components/view-request-history/view-request-history.component'
import { StepperComponent } from './components/stepper/stepper.component'

const routes: Routes = [
  { path: 'stepper', component: StepperComponent },
  { path: 'view-request-history', component: ViewRequestHistoryComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }