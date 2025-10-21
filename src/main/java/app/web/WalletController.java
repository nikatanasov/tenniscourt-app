package app.web;

import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/wallet")
public class WalletController {
    private final UserService userService;
    private final WalletService walletService;

    @Autowired
    public WalletController(UserService userService, WalletService walletService) {
        this.userService = userService;
        this.walletService = walletService;
    }


    @GetMapping
    public ModelAndView getWalletPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("wallet", user.getWallet());
        modelAndView.setViewName("wallet.html");
        return modelAndView;
    }

    @PostMapping("/top-up")
    public String addingMoneyToWallet(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata){
        User user = userService.getById(authenticationMetadata.getUserId());
        walletService.addMoney(user);
        return "redirect:/wallet";
    }
}
