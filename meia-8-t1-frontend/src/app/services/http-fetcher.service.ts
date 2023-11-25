import { IDataDTO } from '../shared/IDataDTO';
import { IDiagnosePrediction } from '../shared/IDiagnosePrediction';
import {IDroolsDataDTO} from "../shared/IDroolsDataDTO";
import {IDroolsHowDTO} from '../shared/IDroolsHowDTO';
import { IPrologBodyDTO } from '../shared/IPrologBodyDTO';
import { Injectable } from '@angular/core';

export var requestHistory: IDiagnosePrediction[] = [];

const droolsBaseURL = 'http://localhost:8081/api/drools';
const prologBaseURL = 'http://localhost:8081/api/prolog';

@Injectable({
  providedIn: 'root'
})

export class HttpFetcherService {

  constructor() {
    this.bootstrap();
  }

  //In order to have some information we have this bootstrap that can be removed
  bootstrap() {
    const dummyData: IDiagnosePrediction = {
      position: 1,
      velocity: 0,
      rpm: 0,
      acceleration: 0,
      pitch: 0,
      gear: [0]
    }

    const dummyData2: IDiagnosePrediction = {
      position: 2,
      velocity: 0,
      rpm: 0,
      acceleration: 0,
      pitch: 0,
      gear: [0]
    }

    requestHistory.push(dummyData);
    requestHistory.push(dummyData2);
  }

  //TODO:Verify this Endpoint!
  async prologGearPredictionStart (requestBody : IPrologBodyDTO, inputRequestHistory: IDataDTO ) : Promise<IDiagnosePrediction | string | any> {
    return fetch(prologBaseURL + '/createFacts', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        },
        body: JSON.stringify(requestBody),
      })
      .then(response => response.json())
      .then(data => {
        if (data !== undefined) return this.prologGearPredictionFetch(inputRequestHistory);
        else throw new Error('Error creating facts!');
      }).catch((error) => {
        console.error('Error: ', error);

        return error;
      });
  }

  //TODO:Verify this Endpoint!
  async prologGearPredictionFetch (input : IDataDTO) : Promise<IDiagnosePrediction | string | any> {;
    return fetch(prologBaseURL + '/startEngine', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        }
      })
      .then(response => response.json())
      .then(data => {
        const gear = this.parsePrologGearResponse(data.facto as string);

        if (gear === 0) {
          throw new Error('Gear not found!');
        }

        let responseDiagnosePrediction: IDiagnosePrediction = {
          position: requestHistory.length + 1,
          velocity: input.velocity,
          rpm: input.rpm,
          acceleration: input.acceleration,
          pitch: input.pitch,
          gear: [gear]
        };

        console.log(data);
        requestHistory.push(responseDiagnosePrediction);

        return responseDiagnosePrediction;
      }).catch((error) => {
        console.error('Error: ', error);

        return error;
      });
  }

  async prologWhyNotFetch(input : IDataDTO) : Promise<IDiagnosePrediction | string | any> {
    const url = prologBaseURL + '/whynot';
    return fetch(url,{
      method: 'POST',
        headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({x: 'gear(meu_veiculo,3)'}),
    })
      .then(response => response.json())
      .then(data => {

        let responseDiagnosePrediction: IDiagnosePrediction = {
          position: requestHistory.length + 1,
          velocity: input.velocity,
          rpm: input.rpm,
          acceleration: input.acceleration,
          pitch: input.pitch,
          gear: data.gear
        };

        console.log("WhyNot: " + JSON.stringify(data));
  
        return responseDiagnosePrediction;
      }).catch((error) => {
        console.error('Error: ', error);

        return error;
      });
  }

  async prologExplanationFetch (nValue: number) : Promise<IDiagnosePrediction | string | any> {
    return fetch(prologBaseURL + '/how/' + nValue, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        }
      })
      .then(response => response.json())
      .then(data => {
        console.log("explanation -> " + JSON.stringify(data));

        return data;
      }).catch((error) => {
        console.error('Error: ', error);

        return error;
      });
  }

  async droolsGearPredictionFetch (input: IDataDTO) : Promise<IDiagnosePrediction> {
    let droolsBody:IDroolsDataDTO = {
      id: Math.floor(Math.random() * 10000),
      rpm:input.rpm,
      speed: input.velocity,
      pitch: input.pitch,
      totalAcceleration: input.acceleration
    };

    return fetch(droolsBaseURL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(droolsBody),
    })
    .then(response => response.json())
    .then(data => {
      console.log('Success:', data);

      let gearPredictions:number[] = [];
      for (const index in data) {
        const gear = this.parseDroolsGearPrediction(data[index].description as string);
        if (gear === 0) {
          throw new Error('Gear not found!');
        }else{
          gearPredictions.push(gear);
        }
      }

      let responseDiagnosePrediction: IDiagnosePrediction = {
       position: requestHistory.length + 1,
       velocity: input.velocity,
       rpm: input.rpm,
       acceleration: input.acceleration,
       pitch: input.pitch,
       gear: gearPredictions
     };

      requestHistory.push(responseDiagnosePrediction);
      console.log('Success parsed:', responseDiagnosePrediction);
      return responseDiagnosePrediction;
    })
    .catch((error) => {
      console.error('Error: ', error);
      return error;
    });
}

 async droolsFuzzyGearPrediction (input: IDataDTO) : Promise<IDiagnosePrediction> {
   let droolsBody:IDroolsDataDTO = {
     id: Math.floor(Math.random() * 10000),
     rpm:input.rpm,
     speed: input.velocity,
     pitch: input.pitch,
     totalAcceleration: input.acceleration
   };

    return fetch(droolsBaseURL + '/fuzzy', {
     method: 'POST',
     headers: {
       'Content-Type': 'application/json',
     },
     body: JSON.stringify(droolsBody),
   })
     .then(response => response.json())
     .then(data => {
       console.log('Success:', data);

       let gearPredictions:number[] = [];
       for (const index in data) {
         const gear = this.parseDroolsGearPrediction(data[index].description as string);
         if (gear === 0) {
           throw new Error('Gear not found!');
         }else{
           gearPredictions.push(gear);
         }
       }

       let responseDiagnosePrediction: IDiagnosePrediction = {
         position: requestHistory.length + 1,
         velocity: input.velocity,
         rpm: input.rpm,
         acceleration: input.acceleration,
         pitch: input.pitch,
         gear: gearPredictions
       };

       requestHistory.push(responseDiagnosePrediction);
       return responseDiagnosePrediction;
     })
     .catch((error) => {
       console.error('Error: ', error);

       return error;
     });
}

  async droolsExplanationFetch() : Promise<IDroolsHowDTO[]> {
    return fetch(droolsBaseURL + '/how', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        }
      })
      .then(response => response.json())
      .then(data => {
        console.log(data);

        return data;
      }).catch((error) => {
        console.error('Error: ', error);

        return error;
      });
  }

  private parsePrologGearResponse(data: string): number {
    const regex = /gear\(meu_veiculo,(\d+)\)/;
    const match = data.match(regex);

    if (match) {
      return match[1] as unknown as number;
    } else {
      return 0;
    }
  }

  private parseDroolsGearPrediction(gearStr: string): number {
    switch(gearStr) {
      case "GEAR_1":
        return 1;
      case "GEAR_2":
        return 2;
      case "GEAR_3":
        return 3;
      case "GEAR_4":
        return 4;
      case "GEAR_5":
        return 5;
      default:
        return 0;
    }
  }

}
