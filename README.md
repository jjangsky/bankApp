## Spring Security의 요청 구조

일반적으로 알고 있는 SpringMvc에서 요청이 들어오면 Front Controller인 DispatcherServlet에 들어오는데 그 이전에 Filter를 거쳐서 요청이 오게 된다.

![Untitled](/image/dispatcher.png)

**DispatcherServlet?**

-   HTTP 프로토콜로 들어오는 모든 요청을 가장 먼저 받아 적합한 컨트롤러에 위임해주는 프론트 컨트롤러(Front Controller)
-   doGet, doPost, doDelete, doPut Method를 가지고 있고 Servlet의 생명 주기를 갖고 있으며 들어온 요청 주소를 파싱하여 해당 Controller로 연결하는 역할 수행

> **Spring Security도 하나의 Filter 이다.**

Controller로 요청이 전달되기 전 Filter에서 인증과 인가에 대한 처리를 하게 된다.

기본적으로 현재 프로젝트에서 사용되고 있는 필터는 `UsernamePasswordAuthenticationFilter`,

`BasicAuthenticationFilter` 두 가지를 사용하고 있다.

→ 이 외에도 Spring Security는 여러 가지의 Filter들이 존재한다.

## UserNamePasswordAuthenticationFilter - 인증

**로그인 요청이 들어왔을 때, 그 값에 따라 인증 권한을 부여하는 필터**

1. 해당 Filter는 `/login` 요청이 들어 왔을 때만 작동하는 Filter 이다.

-   로그인 요청 주소를 변경하여 사용 가능하다.

```java
public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/login");
        this.authenticationManager = authenticationManager;
    }
```

1. 사용자가 보내온 값을 파싱하여 아이디와 패스워드 값을 출력
2. 유저의 정보를 담은 인증 토큰을 생성한다.
    - 여기서의 토큰은 JWT 토큰이 아닌, 사용자임을 인증하는 토큰
3. 이후 UserDetailsService의 loadUserByUsername Method를 호출하여 정보를 DB에서 조회 처리

```java
@Override
public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {
    try {
        ObjectMapper om = new ObjectMapper();
        // 2. 파싱
        UserReqDto.LoginReqDto loginReqDto = om.readValue(request.getInputStream(), UserReqDto.LoginReqDto.class);

        // 3. 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginReqDto.getUsername(),loginReqDto.getPassword());
        // 4. UserDetailsService의 loadUserByUsername 호출
        Authentication authentication =authenticationManager.authenticate(authenticationToken);
        return authentication;
    }catch (Exception e){
       // unsuccessfulAuthentication으로 처리됨
        throw new InternalAuthenticationServiceException(e.getMessage());
    }
}
```

이렇게 처리한 후 성공하면 `authentication` 객체에 인증 정보를 담아서 return 시킨다.

-   이렇게 return하면 최종적으로 SpringContextHolder에 인증 정보가 저장됨

```java
@Override
protected void successfulAuthentication(HttpServletRequest request,HttpServletResponse response,
                                        FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

    LoginUser loginUser = (LoginUser) authResult.getPrincipal();
    String jwtToken = JwtProcess.create(loginUser);
    response.addHeader(JwtVo.Header, jwtToken);

    UserResponseDto.LoginRespDto loginRespDto = new UserResponseDto.LoginRespDto(loginUser.getUser());
    CustomResponseUtil.success(response, loginRespDto);
}
```

여기서 중요한건, JWT 자체가 서버의 의존하지 않는 것이 주 목적이므로 인증 정보를 저장하였더라도 다른 요청이 들어왔을 때, 세션이 만료된 상태이므로 SpringContextHolder에는 인증 정보가 없다.

이후에는 사용자가 들고 있는 **JWT 토큰을 사용하여 SpringContextHolder에 `authentication`저장하며 요청을 처리한다.**

만약 실패한 경우, `InternalAuthenticationServiceException` 발생하여 `unsuccessfulAuthentication` 을 호출하게 된다.

```java
@Override
protected void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException failed) throws IOException, ServletException {
    CustomResponseUtil.fail(response, "로그인 실패", HttpStatus.UNAUTHORIZED);
}
```

## BasicAuthenticationFiler - 인가

해당 필터는 모든 요청에 대해서 Filter가 작용한다.

사용자로부터 요청이 들어오면 해당 필터가 작동하여 사용자가 갖고 있는 토큰을 검증 후, authentication을 생성하고 SpringContextHolder에 저장하는 역할을 한다.

-   이것 또한 세션이라 요청이 종료되는 시점에 SpringContextHolder는 초기화 된다.

이후 Controller에서 토큰에 저장되어 있는 정보를 갖고 요청이 허용되는지 아닌지 판별할 수 있으며 토큰의 있는 정보를 가져와서 사용할 수 도 있다.

```java
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {

    if(isHeaderVerify(request, response)){
        // 토큰이 존재하는 경우
        String token = request.getHeader(JwtVo.Header).replace(JwtVo.TOKEN_PREFIX, ""); // Bearer 제거
        LoginUser loginUser = JwtProcess.verify(token);

        // 임시 세션
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(loginUser.getUsername(), null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication); // 강제 로그인 처리
    }
    chain.doFilter(request, response);

}
```
