document.addEventListener('DOMContentLoaded', ()=>{
    let $btnLogin = document.getElementById('btnLogin');
    $btnLogin.addEventListener('click', () => {
        fn_loginAction();
    });

    $('#edtUserId, #edtPassword').on('keydown', function(e){
        if (e.keyCode == 13){
            fn_loginAction();
        }
    })
});

function fn_loginAction(){
    let param = $('#frmLogin').serializeObject();

    if(Util.isEmpty(param.userId) || Util.isEmpty(param.password)){
        MessageUtil.warning('아이디 또는 비밀번호를\n입력해 주세요.');
        return;
    }

    callModule.call(Util.getRequestUrl('/sys/login/loginAction.do'), param, function (result){
        if(!Util.isEmpty(result)){
            if(result.resultCode === 1 && !Util.isEmpty(result.redirectUrl)){
                localStorage.setItem("jToken", result.result.jToken);
                window.location.href = result.redirectUrl;
            } else {
                MessageUtil.error(result.errorMessage);
            }
        }
    }, true, 'POST');
}
