package com.example.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.Account;
import com.example.entity.AuthorityInfo;
import com.example.exception.CustomException;
import com.example.entity.AdminInfo;
import com.example.entity.UserInfo;

import com.example.service.AdminInfoService;
import com.example.service.UserInfoService;

import com.example.util.PasswordGenerateUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import cn.hutool.json.JSONUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.common.ResultCode.USER_ACCOUNT_ERROR;

@RestController
public class AccountController {

    @Value("${authority.info}")
    private String authorityStr;

	@Resource
	private AdminInfoService adminInfoService;
	@Resource
	private UserInfoService userInfoService;


    @PostMapping("/login")
    public Result<Account> login(@RequestBody Account account, HttpServletRequest request, Model model) {
//        if (StrUtil.isBlank(account.getName()) || StrUtil.isBlank(account.getPassword()) || account.getLevel() == null) {
//            throw new CustomException(ResultCode.PARAM_LOST_ERROR);
//        }
//        Integer level = account.getLevel();
//        Account login = new Account();
//		if (1 == level) {
//			login = adminInfoService.login(account.getName(), account.getPassword());
//		}
//		if (2 == level) {
//			login = userInfoService.login(account.getName(), account.getPassword());
//		}
        //获取当前的用户
        Subject currentUser = SecurityUtils.getSubject();
        //判断用户是否在缓存中
        Account loginUser = (Account) currentUser.getSession().getAttribute(account.getName());
        if(loginUser != null){
            return Result.success(loginUser);
        }

        UsernamePasswordToken token = new UsernamePasswordToken(account.getName(), account.getPassword(),account.getLevel().toString());
        try {
            //主体提交登录请求到SecurityManager
//            token.setRememberMe(rememberMe);
            currentUser.login(token);
        }catch (IncorrectCredentialsException ice){
            model.addAttribute("msg","密码不正确");
            return Result.error("2002","密码不正确");
        }catch(UnknownAccountException uae){
            model.addAttribute("msg","账号不存在");
            return Result.error("2002","账号不存在");
        }catch(AuthenticationException ae){
            model.addAttribute("msg","状态不正常");
            return Result.error("-1","状态不正常");
        }
        if(currentUser.isAuthenticated()){
            System.out.println("认证成功");
//            request.getSession().setAttribute("user", account);
            Integer level = account.getLevel();
            Account login = new Account();
            if (1 == level) {
                login = adminInfoService.findByUserName(account.getName());
            }
            if (2 == level) {
                login = userInfoService.findByUserName(account.getName());
            }
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            session.setAttribute(account.getName(), login);
            return Result.success(login);
        }else{
            token.clear();
            return Result.error();
        }

    }

    @PostMapping("/register")
    public Result<Account> register(@RequestBody Account account) {
        Integer level = account.getLevel();
        Account login = new Account();
		if (1 == level) {
			AdminInfo info = new AdminInfo();
			BeanUtils.copyProperties(account, info);
			login = adminInfoService.add(info);
		}
		if (2 == level) {
			UserInfo info = new UserInfo();
			BeanUtils.copyProperties(account, info);
			login = userInfoService.add(info);
		}

        return Result.success(login);
    }

    @GetMapping("/logout")
    public Result logout(HttpServletRequest request) {

//        request.getSession().setAttribute("user", null);
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
        return Result.success();
    }

    @GetMapping("/auth")
    public Result getAuth(HttpServletRequest request) {

        //获取当前的用户
        Subject currentUser = SecurityUtils.getSubject();
        Account account = (Account) currentUser.getPrincipal();
//        System.out.println(account);

        if(account == null) {
            return Result.error("401", "未登录");
        }
        else {
            Account loginUser = (Account) currentUser.getSession().getAttribute(account.getName());
            return Result.success(loginUser);
        }

    }

    @GetMapping("/getAccountInfo")
    public Result<Object> getAccountInfo(HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        if (account == null) {
            return Result.success(new Object());
        }
        Integer level = account.getLevel();
		if (1 == level) {
			return Result.success(adminInfoService.findById(account.getId()));
		}
		if (2 == level) {
			return Result.success(userInfoService.findById(account.getId()));
		}

        return Result.success(new Object());
    }

    @GetMapping("/getSession")
    public Result<Map<String, String>> getSession(HttpServletRequest request) {
        Account account = (Account) request.getSession().getAttribute("user");
        if (account == null) {
            return Result.success(new HashMap<>(1));
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("username", account.getName());
        return Result.success(map);
    }

    @GetMapping("/getAuthority")
    public Result<List<AuthorityInfo>> getAuthorityInfo() {
        List<AuthorityInfo> authorityInfoList = JSONUtil.toList(JSONUtil.parseArray(authorityStr), AuthorityInfo.class);
        return Result.success(authorityInfoList);
    }

    /**
    * 获取当前用户所能看到的模块信息
    * @param request
    * @return
    */
    @GetMapping("/authority")
    public Result<List<Integer>> getAuthorityInfo(HttpServletRequest request) {
        //获取当前的用户
        Subject currentUser = SecurityUtils.getSubject();
        Account user1 = (Account) currentUser.getPrincipal();
        Account user = (Account) request.getSession().getAttribute(user1.getName());
        if (user == null) {
            return Result.success(new ArrayList<>());
        }
        System.out.println(user.getLevel()+"authority");
        JSONArray objects = JSONUtil.parseArray(authorityStr);
        for (Object object : objects) {
            JSONObject jsonObject = (JSONObject) object;
            if (user.getLevel().equals(jsonObject.getInt("level"))) {
                JSONArray array = JSONUtil.parseArray(jsonObject.getStr("models"));
                List<Integer> modelIdList = array.stream().map((o -> {
                    JSONObject obj = (JSONObject) o;
                    return obj.getInt("modelId");
                    })).collect(Collectors.toList());
                return Result.success(modelIdList);
            }
        }
        return Result.success(new ArrayList<>());
    }

    @GetMapping("/permission/{modelId}")
    public Result<List<Integer>> getPermission(@PathVariable Integer modelId, HttpServletRequest request) {
        List<AuthorityInfo> authorityInfoList = JSONUtil.toList(JSONUtil.parseArray(authorityStr), AuthorityInfo.class);
//        Account user = (Account) request.getSession().getAttribute("user");
        //获取当前的用户
        Subject currentUser = SecurityUtils.getSubject();
        Account user = (Account) currentUser.getPrincipal();
        if (user == null) {
            return Result.success(new ArrayList<>());
        }
        Optional<AuthorityInfo> optional = authorityInfoList.stream().filter(x -> x.getLevel().equals(user.getLevel())).findFirst();
        if (optional.isPresent()) {
            Optional<AuthorityInfo.Model> firstOption = optional.get().getModels().stream().filter(x -> x.getModelId().equals(modelId)).findFirst();
            if (firstOption.isPresent()) {
                List<Integer> info = firstOption.get().getOperation();
                return Result.success(info);
            }
        }
        return Result.success(new ArrayList<>());
    }

    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account info, HttpServletRequest request) {
//        Account account = (Account) request.getSession().getAttribute("user");
        //获取当前的用户
        Subject currentUser = SecurityUtils.getSubject();
        Account account = (Account) currentUser.getPrincipal();
        if (account == null) {
            return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code, ResultCode.USER_NOT_EXIST_ERROR.msg);
        }
        String oldPassword = SecureUtil.md5(info.getPassword());
        if (!oldPassword.equals(account.getPassword())) {
            return Result.error(ResultCode.PARAM_PASSWORD_ERROR.code, ResultCode.PARAM_PASSWORD_ERROR.msg);
        }
        info.setPassword(SecureUtil.md5(info.getNewPassword()));
        Integer level = account.getLevel();
		if (1 == level) {
			AdminInfo adminInfo = new AdminInfo();
			BeanUtils.copyProperties(info, adminInfo);
			adminInfoService.update(adminInfo);
		}
		if (2 == level) {
			UserInfo userInfo = new UserInfo();
			BeanUtils.copyProperties(info, userInfo);
			userInfoService.update(userInfo);
		}

        info.setLevel(level);
        info.setName(account.getName());
        // 清空session，让用户重新登录
        request.getSession().setAttribute("user", null);
        return Result.success();
    }

    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody Account account) {
        Integer level = account.getLevel();
		if (1 == level) {
			AdminInfo info = adminInfoService.findByUserName(account.getName());
			if (info == null) {
				return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code, ResultCode.USER_NOT_EXIST_ERROR.msg);
			}
            String salt = Long.toString(System.currentTimeMillis());
            info.setSalt(salt);
            info.setPassword("123456");
            String newPwd = PasswordGenerateUtil.getPassword(info.getName(),info.getPassword(),salt,2);
            info.setPassword(newPwd);
			adminInfoService.update(info);
		}
		if (2 == level) {
			UserInfo info = userInfoService.findByUserName(account.getName());
			if (info == null) {
				return Result.error(ResultCode.USER_NOT_EXIST_ERROR.code, ResultCode.USER_NOT_EXIST_ERROR.msg);
			}
            String salt = Long.toString(System.currentTimeMillis());
            info.setSalt(salt);
            info.setPassword("123456");
            String newPwd = PasswordGenerateUtil.getPassword(info.getName(),info.getPassword(),salt,2);
            info.setPassword(newPwd);
			userInfoService.update(info);
		}

        return Result.success();
    }
}
