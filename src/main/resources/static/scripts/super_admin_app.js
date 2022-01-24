
$(document).ready(function (){

    let els = document.getElementsByClassName("changeRole");

    Array.prototype.forEach.call(els, function(checkBox) {
        checkBox.addEventListener('change', function (){
            let is_Ad;
            if (this.checked) {
               is_Ad = true;
            } else {
                is_Ad = false;
            }

            let row = checkBox.parentElement.parentElement.parentElement.parentElement;
            console.log(row)
            let us_id = row.getElementsByClassName('user_id')[0].textContent;
            let us_name = row.getElementsByClassName('user_name')[0].textContent;

            const token = $("meta[name='_csrf']").attr("content");
            console.log(token);
            console.log(is_Ad)
            //Собираем объект, чтобы потом преобразовать в json
            let jsonReq = {
                userId : us_id,
                userName : us_name,
                isAdmin : is_Ad
            }
            console.log(JSON.stringify(jsonReq))
            $.ajax({
                type: "POST",
                url : "/admin/users_change",
                headers: {'X-CSRF-TOKEN': token},
                contentType : "application/json; charset=UTF-8",
                dataType : 'json',
                cache : false,
                timeout : 600000,
                data: JSON.stringify(jsonReq)
            });

        });
    });
});
