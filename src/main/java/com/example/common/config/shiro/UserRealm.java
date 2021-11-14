package com.example.common.config.shiro;



import com.example.dao.AdminInfoDao;
import com.example.dao.UserInfoDao;
import com.example.entity.AdminInfo;
import com.example.entity.UserInfo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;


//自定义Realm
public class UserRealm extends AuthorizingRealm {

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    AdminInfoDao adminInfoDao;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了授权");

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        info.addStringPermission("user:add");

        Subject subject = SecurityUtils.getSubject();
        UserInfo currentUser = (UserInfo)subject.getPrincipal();

        return null;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行了认证");

        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
        String level = userToken.getHost();

        if (level.equals("1")) {
            AdminInfo admin = adminInfoDao.findByUsername(userToken.getUsername());
            if (admin == null) {
                return null;
            }
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            session.setAttribute(admin.getName(), admin);

            //密码认证 它自动做了  加密了
            return new SimpleAuthenticationInfo(admin,admin.getPassword(), ByteSource.Util.bytes(admin.getSalt2()), getName());

        }else {
            UserInfo user = userInfoDao.findByUsername(userToken.getUsername());
            if (user == null) {
                return null;
            }
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession();
            session.setAttribute(user.getName(), user);
//            session.setAttribute("user", user);

            //密码认证 它自动做了  加密了
            return new SimpleAuthenticationInfo(user,user.getPassword(), ByteSource.Util.bytes(user.getSalt2()), getName());
        }
    }
}
