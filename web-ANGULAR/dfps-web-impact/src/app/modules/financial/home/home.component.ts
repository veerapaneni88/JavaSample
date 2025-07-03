import { Component, OnInit } from '@angular/core';
import { NavigationService } from 'dfps-web-lib';

@Component({
  selector: 'home',
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

  constructor(private navigationService: NavigationService) { }

  ngOnInit(): void {
    this.navigationService.setTitle('Financial');
  }

}
