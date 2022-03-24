import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { NotifierModule } from 'angular-notifier';
import { FormsModule } from '@angular/forms';
import { AuthenticationGuard } from './guard/authentication.guard';
import { AuthInterceptor } from './interceptor/auth.interceptor';
import { AuthenticationService } from './service/authentication.service';
import { UserService } from './service/userservice.service';
import { UserComponent } from './user/user.component';
import { NotificationService } from './service/notification.service';
import { RegisterComponent } from './register/register.component';
@NgModule({
  declarations: [AppComponent, LoginComponent, UserComponent, RegisterComponent],
  imports: [
    BrowserModule,
        FormsModule,
    HttpClientModule,
    AppRoutingModule,
    NotifierModule.withConfig({
      // Custom options in here
      position: {
        horizontal: { position: 'right' },
        vertical: { position: 'top', distance: 56 },
      },
    }),
  ],
  providers: [NotificationService,
    AuthenticationGuard,
    AuthenticationService,
    UserService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },],
  bootstrap: [AppComponent],
})
export class AppModule {}
