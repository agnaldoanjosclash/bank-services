package com.home.account.service.vatidation;

import com.home.account.data.model.Account;

public record CashOperationsValidation(Account sourceAccount, Account targetAccount, Boolean success, String failMessage) {
}
