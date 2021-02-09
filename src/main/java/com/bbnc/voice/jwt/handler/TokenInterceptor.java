package com.bbnc.voice.jwt.handler;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bbnc.voice.ThreadLocal.ThreadLocalUser;
import com.bbnc.voice.entity.User;
import com.bbnc.voice.jwt.util.TokenUtil;
import com.bbnc.voice.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //跨域请求会首先发一个option请求，直接返回正常状态并通过拦截器
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("token");
        if (token != null){
            boolean result = TokenUtil.verify(token);
            if (result){
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(TokenUtil.TOKEN_SECRET)).withIssuer("auth0").build();//创建token验证器
                DecodedJWT decodedJWT = jwtVerifier.verify(token);
                String username = decodedJWT.getClaim("username").asString();
                User user = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getUsername, username));
                if(user != null) {
                    ThreadLocalUser.set(user);
                    System.out.println("user === " + ThreadLocalUser.getCurrentUser());
                    System.out.println("通过拦截器");
                    return true;
                }
            }
        }
        response.setContentType("application/json; charset=utf-8");
        try {
            JSONObject json = new JSONObject();
            json.put("msg","token验证失败");
            json.put("code","500");
            response.getWriter().append(json.toString());
            System.out.println("认证失败，未通过拦截器");
        } catch (Exception e) {
            return false;
        }
        /**
         * 还可以在此处检验用户存不存在等操作
         */
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadLocalUser.clear();
    }

}
