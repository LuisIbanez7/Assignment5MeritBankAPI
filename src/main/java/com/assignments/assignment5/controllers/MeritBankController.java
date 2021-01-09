package com.assignments.assignment5.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.assignments.assignment5.models.AccountHolder;
import com.assignments.assignment5.models.AccountHoldersContactDetails;
import com.assignments.assignment5.models.AuthenticationRequest;
import com.assignments.assignment5.models.AuthenticationResponse;
import com.assignments.assignment5.models.CDAccount;
import com.assignments.assignment5.models.CDOffering;
import com.assignments.assignment5.models.CheckingAccount;
import com.assignments.assignment5.models.SavingsAccount;
import com.assignments.assignment5.models.SignupRequest;
import com.assignments.assignment5.services.MeritBankService;
import com.assignments.assignment5.services.MyUserDetailsService;
import com.assignments.assignment5.util.JwtUtil;

import Exceptions.AccountNotFoundException;
import Exceptions.ExceedsCombinedBalanceLimitException;

@RestController
public class MeritBankController {
	Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MeritBankService meritBankService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	@Autowired
	private JwtUtil jwtTokenUtil;

	@PreAuthorize("hasRole('admin')")
	@PostMapping("/authenticate/createUser")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		return meritBankService.registerUser(signUpRequest);
	}

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAutheticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (Exception e) {
			// TODO: handle exception
			throw new Exception("incorrect username or password", e);
		}
		final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		return ResponseEntity.ok(new AuthenticationResponse(jwt));

	}

	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/AccountHolders")
	public AccountHolder addAccountHolder(@Valid @RequestBody AccountHolder accountHolder)
			throws AccountNotFoundException {
		return meritBankService.addAccountHolder(accountHolder);
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/AccountHolders")
	public List<AccountHolder> getAccountHolders() {
		return meritBankService.getAccountHolders();
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/AccountHolders/{id}")
	public AccountHolder getAccountHolderById(@PathVariable Integer id) throws AccountNotFoundException {
		AccountHolder ah;
		// debug log - entering
		try {
			// use this only when someone logs in - to have record on log of login
			log.info("Entered /AccountHolders/{1} End Point");
			ah = meritBankService.getAccountHolderById(id);
		} catch (Exception e) {
			// error log - there's been an error + exception
			log.debug("getAccountById Started" + e);
			throw new AccountNotFoundException("Account id not found");
		}
		log.info("Entered /AccountHolders/{1} End Point");
		// debug log - returning
		return ah;
	}

//	@GetMapping(value = "/ContactDetails")
//	public List<AccountHoldersContactDetails> getAccountHoldersContactDetails(){
//		return meritBankService.getAccountHoldersContactDetails();
//	}

//	@ResponseStatus(HttpStatus.OK)
//	@PostMapping(value = "/ContactDetails/{id}")
//	public AccountHoldersContactDetails postContactDetails(@Valid @RequestBody AccountHoldersContactDetails ahContactDetails,
//			@PathVariable Integer id){
//		return meritBankService.postContactDetails(ahContactDetails, id);
//	}

	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/AccountHolders/{id}/CheckingAccounts")
	public CheckingAccount postCheckingAccount(@Valid @RequestBody CheckingAccount checkingAccount,
			@PathVariable Integer id) throws ExceedsCombinedBalanceLimitException {
		return meritBankService.postCheckingAccount(checkingAccount, id);
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/AccountHolders/{id}/CheckingAccounts")
	public List<CheckingAccount> getCheckingAccountsById(@PathVariable Integer id) throws AccountNotFoundException {
		try {
			return meritBankService.getCheckingAccountsById(id);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/AccountHolders/{id}/SavingsAccounts")
	public SavingsAccount postSavingsAccount(@Valid @RequestBody SavingsAccount savingsAccount, @PathVariable int id)
			throws ExceedsCombinedBalanceLimitException {
		return meritBankService.postSavingsAccount(savingsAccount, id);
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/AccountHolders/{id}/SavingsAccounts")
	public List<SavingsAccount> getSavingsAccountsById(@PathVariable int id) throws AccountNotFoundException {
		return meritBankService.getSavingsAccountsById(id);
	}

	@PreAuthorize("hasRole('admin')")
	@PostMapping(value = "/AccountHolders/{id}/CDAccounts")
	public CDAccount postCDAccount(@Valid @RequestBody CDAccount cdAccount, @PathVariable int id)
			throws AccountNotFoundException, ExceedsCombinedBalanceLimitException {
		return meritBankService.postCDAccount(cdAccount, id);
	}

	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.CREATED)
	@GetMapping(value = "/AccountHolders/{id}/CDAccounts")
	public List<CDAccount> getCDAccountsbyId(@PathVariable int id) {
		return meritBankService.getCDAccountsbyId(id);
	}

	// @PreAuthorize("hasRole('AccountHolder')")
	@ResponseStatus(HttpStatus.CREATED)
	@GetMapping(value = "/Me")
	public AccountHolder getMyAccountInfo(HttpServletRequest request) {
		return meritBankService.getMyAccountInfo(request);
	}
	
	
	
	@PreAuthorize("hasRole('AccountHolder')")
	@ResponseStatus(HttpStatus.CREATED)
	@GetMapping(value = "/Me/CheckingAccount")
	public List<CheckingAccount> getMyCheckingAccounts(HttpServletRequest request) {
		return meritBankService.getMyCheckingAccounts(request);
	}
	
	@PreAuthorize("hasRole('AccountHolder')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/Me/CheckingAccount")
	public CheckingAccount postMyCheckingAccount(HttpServletRequest request, CheckingAccount checkingAccount)
			throws ExceedsCombinedBalanceLimitException {
		
		return meritBankService.postMyCheckingAccount(request, checkingAccount);
	}

	
	
	@PreAuthorize("hasRole('admin')")
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/CDOfferings")
	public CDOffering postCDOffering(@Valid @RequestBody CDOffering cdOffering) {
		return meritBankService.postCDOffering(cdOffering);
	}

	@PreAuthorize("hasRole('admin') or hasRole('AccountHolder')")
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/CDOfferings")
	public List<CDOffering> getCDOfferings() {
		return meritBankService.getCDOfferings();
	}

}
