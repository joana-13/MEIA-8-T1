import { Component, OnInit, ViewChild } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

import {BreakpointObserver} from '@angular/cdk/layout';
import { HttpFetcherService } from '../../services/http-fetcher.service';
import { IDataDTO } from 'src/app/shared/IDataDTO';
import {IDiagnosePrediction} from "../../shared/IDiagnosePrediction";
import {IDroolsHowDTO} from "../../shared/IDroolsHowDTO";
import { IPrologBodyDTO } from 'src/app/shared/IPrologBodyDTO';
import { MatStepper } from '@angular/material/stepper';
import {Observable} from 'rxjs';
import {StepperOrientation} from '@angular/material/stepper';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-stepper',
  templateUrl: './stepper.component.html',
  styleUrls: ['./stepper.component.scss']
})

export class StepperComponent implements OnInit {
  formGroup!: FormGroup;
  inputData! : IDataDTO;
  isFormCompleted : boolean = false;
  isDiagnoseCompleted : boolean = false;
  stepperOrientation: Observable<StepperOrientation>;

  droolsResponse : string[] = [];
  droolsExplanation : IDroolsHowDTO[] | undefined;
  prologResponse : IDiagnosePrediction | undefined;
  prologExplanation : string[] | undefined;

  @ViewChild(MatStepper) stepper!: MatStepper;

  constructor(private _formBuilder: FormBuilder,
              private httpFetcherService: HttpFetcherService, breakpointObserver: BreakpointObserver) {
                this.stepperOrientation = breakpointObserver
                  .observe('(min-width: 800px)')
                  .pipe(map(({matches}) => (matches ? 'horizontal' : 'vertical')));
  }

  ngOnInit() {
    this.formGroup = this._formBuilder.group({ firstValue: [null, [Validators.required, Validators.min(0), Validators.pattern("^(-)?[0-9]+(\.[0-9]+)?$")]],
                                               secondValue: [null, [Validators.required, Validators.min(0), Validators.pattern("^(-)?[0-9]+(\.[0-9]+)?$")]],
                                               thirdValue: [null, [Validators.min(0), Validators.pattern("^(-)?[0-9]+(\.[0-9]+)?$")]],
                                               fourthValue: [null, [Validators.min(0), Validators.pattern("^(-)?[0-9]+(\.[0-9]+)?$")]]});

    this.inputData = {} as IDataDTO;
  }

  setValuesAndMakeRequests() {
    this.inputData.velocity = this.formGroup.get('firstValue')?.value;
    this.inputData.rpm = this.formGroup.get('secondValue')?.value;
    this.inputData.acceleration = this.formGroup.get('thirdValue')?.value;
    this.inputData.pitch = this.formGroup.get('fourthValue')?.value;

    this.isFormCompleted = true;

    //First requests
    this.droolsGearPredictionFetch();
    this.prologGearPredictionFetch();
  }

  setDiagnoseFlag() {
    this.isDiagnoseCompleted = true;
  }


  resetFormData() {
    this.formGroup = this._formBuilder.group({ firstValue: [null, [Validators.required, Validators.min(0), Validators.pattern("^(-)?[0-9]*$")]],
                                               secondValue: [null, [Validators.required, Validators.min(0), Validators.pattern("^(-)?[0-9]*$")]],
                                               thirdValue: [null, [Validators.min(0), Validators.pattern("^(-)?[0-9]*$")]],
                                               fourthValue: [null, [Validators.min(0), Validators.pattern("^(-)?[0-9]*$")]]});
  }

  resetStepper() {
    this.resetFormData();
    this.stepper.reset();
  }

  prologGearPredictionFetch() {
    this.prologExplanation = undefined;
    this.httpFetcherService.prologGearPredictionStart(this.prologGearPredictionBodyParse(),this.inputData).then((data : any) => {
      this.prologResponse = data;
    }, (error : any) => {
      this.prologResponse = error;
    }
    );
  }

  prologExplanationFetch() {
    this.httpFetcherService.prologExplanationFetch(this.countNumberOfFacts(this.inputData)).then((data : any) => {
      const regex = /\*+/g;
      let parsedRes = data.como.replace(regex,"");
      this.prologExplanation = parsedRes.split("\n");
    }, (error : any) => {
      this.prologResponse = error;
    }
    );
  }

  prologWhyNotFetch() {
     this.httpFetcherService.prologWhyNotFetch(this.inputData).then((data : any) => {
      this.prologResponse = data;
     }, (error : any) => {
      this.prologResponse = error;
     }
    );
  }

  droolsGearPredictionFetch() {
    this.droolsExplanation = undefined;
    this.httpFetcherService.droolsGearPredictionFetch(this.inputData).then((data : IDiagnosePrediction) => {
      this.droolsResponse = this.droolsGearPredictionResponseParse(data, "without fuzzy logic");
    }, (error : any) => {
      this.droolsResponse = error;
    }
    );
  }

  droolsFuzzyGearPredictionFetch() {
    this.droolsExplanation = undefined;
    this.httpFetcherService.droolsFuzzyGearPrediction(this.inputData).then((data : IDiagnosePrediction) => {
      console.log(data)
      this.droolsResponse = this.droolsGearPredictionResponseParse(data, "using fuzzy logic");
    }, (error : any) => {
      this.droolsResponse = error;
    }
    );
  }


  droolsExplanationFetch() {
    this.droolsResponse = [];
    this.httpFetcherService.droolsExplanationFetch().then((data : IDroolsHowDTO[]) => {
       this.droolsExplanation = data;
       console.log(this.droolsExplanation);
     } , (error : any) => {
       this.droolsExplanation = error;
     }
     );
   }

  private prologGearPredictionBodyParse() : IPrologBodyDTO {
    let factNumber = 0;

    factNumber++;
    const velocity = "facto(" + factNumber + ",velocidade(meu_veiculo," + this.inputData.velocity + ")).";

    factNumber++;
    const rpm = "facto(" + factNumber + ",rpm(meu_veiculo," + this.inputData.rpm + ")).";

    let body: IPrologBodyDTO = {
      velocity: velocity,
      rpm: rpm,
    };

    if(this.inputData.acceleration != null) {
      factNumber++;
      const acceleration = "facto(" + factNumber + ",acceleration(meu_veiculo," + this.inputData.acceleration + ")).";
      body = {
        ...body,
        acceleration: acceleration,
      }
    }

    if (this.inputData.pitch != null) {
      factNumber++;

      const pitch = "facto(" + factNumber + ",pitch(meu_veiculo," + this.inputData.pitch + ")).";
      body = {
        ...body,
        pitch: pitch,
        n : "ultimo_facto(" + factNumber + ")."
      }
    }

    return body;
  }

  private droolsGearPredictionResponseParse(gearInput : IDiagnosePrediction | any, typeEngine:string) : string[] {
    let listPredictions:string[]  = [];
    console.log(gearInput)
    for (const gearIndex in gearInput.gear) {
      let gearPrediction: number = gearInput.gear[gearIndex];
      switch(gearPrediction) {
        case 1:
          listPredictions.push(`The suggested gear ${typeEngine} is: 1`);
          break;
        case 2:
          listPredictions.push(`The suggested gear ${typeEngine} is: 2`);
          break;
        case 3:
          listPredictions.push(`The suggested gear ${typeEngine} is: 3`);
          break;
        case 4:
          listPredictions.push(`The suggested gear ${typeEngine} is: 4`);
          break;
        case 5:
          listPredictions.push(`The suggested gear ${typeEngine} is: 5`);
          break;
        default:
          listPredictions.push("The expert system couldn't find a gear for the data input.");
      }
    }
    if(listPredictions.length === 0){
      listPredictions.push("The expert system couldn't find a gear for the data inputted.");
    }

    return listPredictions;
  }

  private generateDiagnoseMessage(inputStr : string) : string {
    const regex = /gear\(meu_veiculo,(\d+)\)/;
    const match = inputStr.match(regex);

    if (match) {
      const gearNumber = match[1];
      return `The suggest gear for the data inputted is: ${gearNumber}`;
    } else {
      return "The expert system couldn't find a gear for the data inputted.";
    }
  }

  private countNumberOfFacts(data : IDataDTO) : number {
    let numberOfFacts = 1;

    Object.keys(data).forEach(() => {
      numberOfFacts++;
    });

    return numberOfFacts;
  }

}
