import { Component, ViewChild, AfterViewInit, OnInit  } from '@angular/core';
import { requestHistory } from '../../services/http-fetcher.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { IDiagnosePrediction } from '../../shared/IDiagnosePrediction';


@Component({
  selector: 'app-view-request-history',
  templateUrl: './view-request-history.component.html',
  styleUrls: ['./view-request-history.component.scss']
})
export class ViewRequestHistoryComponent implements AfterViewInit, OnInit  {

  displayedColumns: string[] = ['position', 'velocity', 'rpm', 'acceleration', 'pitch', 'gear'];
  dataSource = new MatTableDataSource<IDiagnosePrediction>([]);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngOnInit(): void {
    this.dataSource.data = requestHistory;
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

}
