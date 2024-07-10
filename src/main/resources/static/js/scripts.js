$(document).ready(function() {
    $(".results").hide();
    $('.errorDiv').hide();
    $("#btn").click(function(event) {
        event.preventDefault();
        let vd1 = document.getElementById("v");
        let vd2 = document.getElementById("toTextValue");
        let vd3 = document.getElementById("toPdfValue");
        let formDataV = {
            'n': vd1.value,
            'toText': vd2.checked,
            'toPdf': vd3.checked
        };
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "/api/run",
            data: JSON.stringify(formDataV),
            dataType: 'json',
            cache: false,
            timeout: 600000,
            success: function(data) {
                if(data.pdfFilePath) {
                    downloadPdfFile(data.pdfFilePath);
                }
                $('.results').show();
                $('#textArea').html(data.textFormat);
                $('#latexArea').html(data.latexFormat);
                $(".errorDiv").hide();
            },
            error: function(e) {
                $('#textArea').html("");
                $('#latexArea').html("");
                $('.results').hide();
                $('.errorDiv').html(JSON.parse(e.responseText).message);
                $(".errorDiv").show();
            }
        });
    });
});

function downloadPdfFile(filePath) {
    let data = {
        'filePath': filePath,
    };

    $.ajax({
        url: '/api/download-pdf',
        type: 'POST',
        contentType: "application/json",
        data: JSON.stringify(data),
        xhrFields: {
            responseType: 'blob'
        },
        success: function(blob) {
            let downloadUrl = URL.createObjectURL(blob);
            let a = document.createElement('a');
            a.href = downloadUrl;
            a.download = 'latex.pdf';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(downloadUrl);
        },
        error: function(xhr, status, error) {
            console.error("Dosya indirilirken bir hata oluştu:", error);
        }
    });
}

