export interface IDiagnosePrediction {
    position: number;
    velocity: number;
    rpm: number;
    acceleration?: number;
    pitch?: number;
    gear: number[];
  }
