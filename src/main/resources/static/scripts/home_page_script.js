$(document).ready(function () {
    const token = $("meta[name='_csrf']").attr("content");
    let els = $(".cert-list-row");

    $('.reload-button').on('click', function (){
        document.location.reload();
    });

    let remember_prev_selected_row;

    let remember_cert_id;

    Array.prototype.forEach.call(els, function (certRow) {

        certRow.addEventListener('click', function (ev) {
            const  cert_id = $(this).attr('id');
            remember_cert_id=cert_id;
            $(remember_prev_selected_row).removeClass('selected-row');
            $(this).addClass('selected-row');
            remember_prev_selected_row = $(this);

            const verified = $(this).attr('data-cert-verified');

            $.ajax({
                type: "POST",
                url: "/home/cert/" + cert_id,
                headers: {'X-CSRF-TOKEN': token},
                contentType: "application/json; charset=UTF-8",
                cache: false,
                timeout: 600000,
                success: function (data) {

                    let tableHTML = createTable(data);
                    let buttonsHTML = createButtons(verified, cert_id);

                    $('#prev-table-image').css('display', 'none');
                    $('#cert-table').html(tableHTML);
                    $('#download-button-wrap').html(buttonsHTML);
                    $('#download-root-cert-button-wrap').css('display', 'block');
                }
            });


        });


    });


});

function createTable(data) {

    return `<tr><th scope="row">Дата подачи заявки на сертификат</th>` +
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

function createButtons(verified, cert_id) {

    if (parseInt(verified) === 0) {
        return `<h6 class="text-muted">Сертификат еще не одобрен администратором</h6>` +
            `<div class="col-12"></div>` +
            `<a href="#" class="btn btn-outline-success btn-sm disabled" role="button" aria-pressed="true">Скачать</a>`;
    } else if (parseInt(verified) === 1) {
         return  `<a href="/home/cert/download/${cert_id}" class="btn btn-outline-success btn-sm" role="button" aria-pressed="true">Скачать</a>`;
    }else if (parseInt(verified) === -1) {
        return `<h6 class="text-danger">Сертификат был отклонен</h6>` +
            `<div class="col-12"></div>` +
            `<a href="#" class="btn btn-outline-success btn-sm disabled" role="button" aria-pressed="true">Скачать</a>`;
    }


}