<form class="two-step-signin-form" method="POST" action="{{ actions.signInWithEmailAndPassword }}">

    {{> components/two-step-signin/_helpers }}

    <div class="form-field-wrap">
      <input class="u-h" type="email" name="email" autocomplete="username" autocapitalize="off" autocorrect="off" spellcheck="false" value="{{email}}" />
      <label class="form-field-wrap__title" for="signin_field_email">{{twoStepSignInPageText.passwordFieldTitle}}</label>
      <input class="form-input form-field-wrap__field" type="password" name="password" autocomplete="password" autocapitalize="off" autocorrect="off" spellcheck="false" value="{{password}}" />
      <div class="u-flexrow form-field-wrap__footer">
        <label class="form-checkbox">
          <input class="form-checkbox__input" type="checkbox" name="rememberMe" checked="checked" value="true"/>
          <span class="form-checkbox__label">{{twoStepSignInPageText.rememberMe}}</span>
        </label>
        <a class="link" href="{{forgotPasswordUrl}}">{{twoStepSignInPageText.forgottenPassword}}</a>
      </div>
    </div>
    <div class="form-field-wrap">
      <a class="form-button form-field-wrap__field" type="submit">{{twoStepSignInPageText.changeEmail}} {{> components/icon/arrow-inline }}</a>
    </div>

</form>
