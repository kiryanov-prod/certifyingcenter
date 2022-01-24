$(document).ready(function () {
    updateMessageEmpty();
    const token = $("meta[name='_csrf']").attr("content");

    let els = $(".cert-list-row");
    let remember_prev_cert_id = -1;
    let remember_cert_id;
    let remember_old_verify;

    Array.prototype.forEach.call(els, function (certRow) {
        certRow.addEventListener('click', function (ev) {

            const current_cert_id = certRow.id;
            if (remember_prev_cert_id !== -1) {
                $('#' + remember_prev_cert_id + ' i').css('opacity', '0');
            }
            $('#' + current_cert_id + ' i').css('opacity', '1');
            remember_prev_cert_id = current_cert_id;
            remember_cert_id = current_cert_id;
            remember_old_verify = $(certRow).attr('data-cert-verified');

            $.ajax({
                type: "POST",
                url: "/admin/certificate/" + current_cert_id,
                headers: {'X-CSRF-TOKEN': token},
                contentType: "application/json; charset=UTF-8",
                cache: false,
                timeout: 600000,
                success: function (data) {

                    let tableHTML = createTable(data);
                    let buttonsHTML = createButtons(remember_old_verify);
                    $('#prev-table-image').css('display', 'none');
                    $('#cert-table').html(tableHTML);
                    $('#change-verify-buttons-wrap').html(buttonsHTML);
                }
            });

            ev.preventDefault();
        });
    });

    $(document).on('click', '.verified-change', function () {
        const buttonID = $(this).attr('id');

        let new_verify_value;
        if (buttonID === 'approve-button') {
            new_verify_value = 1;
        } else if (buttonID === 'reject-button') {
            new_verify_value = -1;
        }

        const reqChangingVerify = {
            certId: remember_cert_id,
            newVerifyNumber: new_verify_value
        };

        console.log(JSON.stringify(reqChangingVerify));

        $.ajax({
            type: "POST",
            url: "/admin/certificate/change_verify",
            headers: {'X-CSRF-TOKEN': token},
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            data: JSON.stringify(reqChangingVerify),
            cache: false,
            timeout: 600000,
            success: function (data) {
                updateCertList(remember_cert_id, new_verify_value);
                const buttonsHTML = createButtons(new_verify_value);
                $('#change-verify-buttons-wrap').html(buttonsHTML);
            }
        });
    });
});

function updateCertList(certId, newVerify) {
    let certRow = $('#' + certId);
    if (newVerify === 1) {
        $(certRow).detach().appendTo("#list-group-approved-cert");
    } else if (newVerify === -1) {
        $(certRow).detach().appendTo("#list-group-rejected-cert");
    }

    updateMessageEmpty();
}
//quantity
function updateMessageEmpty(){
    const subListGroupCount  = $('#list-group-submitted-cert  .cert-list-row').length;
    const appListGroupCount  = $('#list-group-approved-cert   .cert-list-row').length;
    const rejListGroupCount  = $('#list-group-rejected-cert   .cert-list-row').length;

    if(subListGroupCount > 0){
        $('#list-group-submitted-cert .cert-empty').css('display', 'none');
    }else {
        $('#list-group-submitted-cert .cert-empty').css('display', 'block');
    }

    if(appListGroupCount > 0){

        $('#list-group-approved-cert .cert-empty').css('display', 'none');
    }else {
        $('#list-group-approved-cert .cert-empty').css('display', 'block');
    }

    if(rejListGroupCount > 0){

        $('#list-group-rejected-cert .cert-empty').css('display', 'none');
    }else {
        $('#list-group-rejected-cert .cert-empty').css('display', 'block');
    }


}


function createTable(data) {
    return `<tr><th scope="row">Заявка студента</th>` +
        `<td>${data.nameAndLastName}</td></tr>` +
        `<tr><th scope="row">Дата подачи заявки на сертификат</th>` +
        `<td>${data.timeOfApplication}</td></tr><tr>` +
        `<th scope="row">Дата начала действия сертификата</th>` +
        `<td>${data.timeOfNotBefore}</td></tr><tr>` +
        `<th scope="row">Дата окончания действия сертификата</th>` +
        `<td>${data.timeOfNotAfter}</td></tr><tr>` +
        `<th scope="row">Доменное имя</th>` +
        `<td>${data.domainName}</td></tr><tr>` +
        `<th scope="row">Двухбуквенный код страны</th>` +
        `<td>${data.country}</td></tr><tr>` +
        `<th scope="row">Город</th>` +
        `<td>${data.city}</td></tr><tr>` +
        `<th scope="row">Организация</th>` +
        `<td>${data.organization}</td></tr><tr>` +
        `<th scope="row">Отдел</th>` +
        `<td>${data.department}</td></tr><tr>` +
        `<th scope="row">Email</th>` +
        `<td>${data.email}</td></tr>`;
}

function createButtons(verified) {
    let butt_1_DisablesStatus = '';
    let butt_2_DisablesStatus = '';

    if (parseInt(verified) === 1) {
        butt_1_DisablesStatus = 'disabled';
    } else if (parseInt(verified) === -1) {
        butt_2_DisablesStatus = 'disabled';
    }

    return `<div class="row justify-content-center">` +
        `<div class="button-wrap">` +
        `<button id="approve-button" class="btn btn-outline-success btn-sm verified-change" ${butt_1_DisablesStatus} >` +
        `Одобрить` +
        `</button>` +
        `<button id="reject-button" class="btn btn-outline-danger btn-sm verified-change" ${butt_2_DisablesStatus} >` +
        `Отказать` +
        `</button></div></div>`;
}