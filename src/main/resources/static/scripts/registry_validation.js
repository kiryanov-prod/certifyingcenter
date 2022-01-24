$(document).ready(function (){

    $('#nameId').on('focus', function (){
        document.getElementById("name-error").style.display = "none";
    });
    $('#inputPassword').on('focus', function (){
        document.getElementById("password-error").style.display = "none";
    });
    $('#inputConfirmPassword').on('focus', function (){
        document.getElementById("password-error").style.display = "none";
    });




    $('#submit_form_reg').on('click', function (event){

        const inputPassword1 = document.getElementById("inputPassword").value
        const inputPassword2 = document.getElementById("inputConfirmPassword").value
        const nameValue = document.getElementById("nameId").value

        if(nameValue===null||nameValue===''){
            document.getElementById("name-error").style.display = "block";
            return false;
        }

        if(inputPassword1 === inputPassword2){
            return true;
        }
        document.getElementById("password-error").style.display = "block";

        return false;
    });
});