package com.assignments.assignment5.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.assignments.assignment5.repository.UserRepository;
import com.assignments.assignment5.util.JwtUtil;
import com.assignments.assignment5.models.Role;

import com.assignments.assignment5.models.User;
import com.assignments.assignment5.models.AccountHolder;
import com.assignments.assignment5.models.AccountHoldersContactDetails;
import com.assignments.assignment5.models.AuthenticationRequest;
import com.assignments.assignment5.models.CDAccount;
import com.assignments.assignment5.models.CDOffering;
import com.assignments.assignment5.models.CheckingAccount;
import com.assignments.assignment5.models.ERole;
import com.assignments.assignment5.models.SavingsAccount;
import com.assignments.assignment5.models.SignupRequest;
import com.assignments.assignment5.repository.AccountHolderRepository;
import com.assignments.assignment5.repository.AccountHoldersContactDetailsRepository;
import com.assignments.assignment5.repository.CDAccountRepository;
import com.assignments.assignment5.repository.CDOfferingRepository;
import com.assignments.assignment5.repository.CheckingAccountRepository;
import com.assignments.assignment5.repository.SavingsAccountRepository;
import com.assignments.assignment5.repository.RoleRepository;


import Exceptions.AccountNotFoundException;
import Exceptions.ExceedsCombinedBalanceLimitException;

@Service
public class MeritBankService {
	@Autowired
	private AccountHoldersContactDetailsRepository ahContactDetailsrepository;
	@Autowired
	private AccountHolderRepository accountHolderRepository;
	@Autowired
	private SavingsAccountRepository savingsAccountRepository;
	@Autowired
	private CheckingAccountRepository checkingAccountRepository;
	@Autowired
	private CDAccountRepository cdAccountRepository;
	@Autowired
	private CDOfferingRepository cdOfferingRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

	public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body("Error: Username is already taken!");
		}
		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getPassword());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.AccountHolder)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.admin)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "AccountHolder":
					Role userRole = roleRepository.findByName(ERole.AccountHolder)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});

		}

		user.setActive(signUpRequest.isActive());
		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok("User registered successfully!");
	}
	
	public AccountHolder addAccountHolder(AccountHolder accountHolder) throws AccountNotFoundException {
		accountHolder.setUser(userRepository.findById(accountHolder.getUser().getId())
				.orElseThrow(() -> new AccountNotFoundException("Error: User is not found.")));
		return accountHolderRepository.save(accountHolder);
	}
	public List<AccountHolder> getAccountHolders(){
		return accountHolderRepository.findAll();
	}
	public AccountHolder getAccountHolderById(Integer id) throws AccountNotFoundException {
		return getById(id);
	}
	public AccountHolder getById(Integer id) {
		return accountHolderRepository.findById(id).orElse(null);
	}
//	public AccountHoldersContactDetails postContactDetails(@Valid @RequestBody AccountHoldersContactDetails ahContactDetails,
//			@PathVariable Integer id){
//		AccountHolder ah = getById(id);
//		ahContactDetails.setAccountHolder(ah);
//		//accountHolderRepository.save(ah);
//		ahContactDetailsrepository.save(ahContactDetails);
//		return ahContactDetails;
//	}
	public List<AccountHoldersContactDetails> getAccountHoldersContactDetails(){
		return ahContactDetailsrepository.findAll();
	}
	public CheckingAccount postCheckingAccount(CheckingAccount checkingAccount, Integer id) throws ExceedsCombinedBalanceLimitException{
		AccountHolder ah = getById(id);
		if (ah.getCombinedBalance() + checkingAccount.getBalance() > 250000) {
			throw new ExceedsCombinedBalanceLimitException("Balance exceeds limit");
		}
		ah.setCheckingAccounts((Arrays.asList(checkingAccount)));
		checkingAccount.setAccountHolder(ah);
		checkingAccountRepository.save(checkingAccount);
		return checkingAccount;
	}
	public List<CheckingAccount> getCheckingAccountsById(@PathVariable Integer id){
		return getById(id).getCheckingAccounts();
	}
	
	public SavingsAccount postSavingsAccount(SavingsAccount savingsAccount, int id) throws ExceedsCombinedBalanceLimitException{
		AccountHolder ah = getById(id);
		if (ah.getCombinedBalance() + savingsAccount.getBalance() > 250000) {
			throw new ExceedsCombinedBalanceLimitException("Balance exceeds limit");
		}
		ah.setSavingsAccounts((Arrays.asList(savingsAccount)));
		savingsAccount.setAccountHolder(ah);
		
		savingsAccountRepository.save(savingsAccount);
		return savingsAccount;
	}
	
	public List<SavingsAccount> getSavingsAccountsById(int id) throws AccountNotFoundException {
		return getById(id).getSavingsAccounts();
	}
	public CDAccount postCDAccount(CDAccount cdAccount, int id)
			throws AccountNotFoundException, ExceedsCombinedBalanceLimitException {
		AccountHolder ah = getById(id);
		if (ah.getCombinedBalance() + cdAccount.getBalance() > 250000) {
			throw new ExceedsCombinedBalanceLimitException("Balance exceeds limit");
		}
		ah.setcDAccounts(Arrays.asList(cdAccount));
		cdAccount.setAccountHolder(ah);
		cdAccountRepository.save(cdAccount);
		return cdAccount;
	}
	public List<CDAccount> getCDAccountsbyId(int id) {
		return getById(id).getcDAccounts();
	}
	
	public AccountHolder getMyAccountInfo(HttpServletRequest request) {
		final String authorizationHeader = request.getHeader("Authorization");

		String username = null;
		String jwt = null;
		AccountHolder ah = null;
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			username = jwtUtil.extractUsername(jwt);
		}
        if (username != null) {

            User user = this.userRepository.findByUsername(username).orElseThrow(null);
            ah = user.getAccountHolder();
//            ah = accountHolderRepository.findById(user.getId()).orElseThrow(null);
        }
		return ah;
	}
	
	public List<CheckingAccount> getMyCheckingAccounts(HttpServletRequest request) {
		AccountHolder ah = getMyAccountInfo(request);
		return ah.getCheckingAccounts();
	}
	
	public CheckingAccount postMyCheckingAccount(HttpServletRequest request, CheckingAccount checkingAccount)
			throws ExceedsCombinedBalanceLimitException {
		
		AccountHolder ah = getMyAccountInfo(request);
		if (ah.getCombinedBalance() + checkingAccount.getBalance() > 250000) {
			throw new ExceedsCombinedBalanceLimitException("Balance exceeds limit");
		}
		ah.setCheckingAccounts((Arrays.asList(checkingAccount)));
		checkingAccount.setAccountHolder(ah);
		checkingAccountRepository.save(checkingAccount);
		return checkingAccount;
	}
	public SavingsAccount postMySavingsAccount(HttpServletRequest request, SavingsAccount savingsAccount)
			throws ExceedsCombinedBalanceLimitException {
		
		AccountHolder ah = getMyAccountInfo(request);
		if (ah.getCombinedBalance() + savingsAccount.getBalance() > 250000) {
			throw new ExceedsCombinedBalanceLimitException("Balance exceeds limit");
		}
		ah.setSavingsAccounts((Arrays.asList(savingsAccount)));
		savingsAccount.setAccountHolder(ah);
		savingsAccountRepository.save(savingsAccount);
		return savingsAccount;
	}

	public List<SavingsAccount> getMySavingsAccounts(HttpServletRequest request) {
		AccountHolder ah = getMyAccountInfo(request);
		return ah.getSavingsAccounts();
	}
	
	public CDAccount postMyCDAccounts(HttpServletRequest request, CDAccount cDAccount)
			throws ExceedsCombinedBalanceLimitException {
		
		AccountHolder ah = getMyAccountInfo(request);
		if (ah.getCombinedBalance() + cDAccount.getBalance() > 250000) {
			throw new ExceedsCombinedBalanceLimitException("Balance exceeds limit");
		}
		ah.setcDAccounts((Arrays.asList(cDAccount)));
		cDAccount.setAccountHolder(ah);
		cdAccountRepository.save(cDAccount);
		return cDAccount;
	}
	
	
	public List<CDAccount> getMyCDAccount(HttpServletRequest request) {
		AccountHolder ah = getMyAccountInfo(request);
		return ah.getcDAccounts();
	}
	
	public CDOffering postCDOffering(CDOffering cdOffering) {
		return cdOfferingRepository.save(cdOffering);
	}
	public List<CDOffering> getCDOfferings() {
		return cdOfferingRepository.findAll();
	}
}
