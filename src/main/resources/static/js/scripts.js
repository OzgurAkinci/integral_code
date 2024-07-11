$(document).ready(function() {
    appendArea();

    $(".spinner").hide();
    $(".results").hide();
    $('.errorDiv').hide();
    $("#btn").click(function(event) {
        event.preventDefault();
        $(".spinner").show();
        let nValue = document.getElementById("nValue");
        let hValue = document.getElementById("hValue");
        let fValue = document.getElementById("fValue");
        let toTextValue = document.getElementById("toTextValue");
        let toPdfValue = document.getElementById("toPdfValue");
        const yValueElements = document.getElementsByClassName("dynamicInput");
        const yValues = [];
        for(let i=0; i<yValueElements.length; i++) {
            yValues.push(yValueElements.item(i).value)
        }

        let formDataV = {
            'n': nValue.value,
            'h': hValue.value,
            'f': fValue.value,
            'yvalues': yValues,
            'toText': toTextValue.checked,
            'toPdf': toPdfValue.checked
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
                $(".spinner").hide();
            },
            error: function(e) {
                $('#textArea').html("");
                $('#latexArea').html("");
                $('.results').hide();
                $('.errorDiv').html(JSON.parse(e.responseText).message);
                $(".errorDiv").show();
                $(".spinner").hide();
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

function appendArea() {
    let nValue = document.getElementById("fValue");
    removeElementsByClass("dynamicInputDiv");
    if(nValue && nValue.value) {
        for(let i=0; i<nValue.value; i++) {
            const div = document.createElement("div");
            div.className = "dynamicInputDiv form-input";

            let inputId = "inputId-" + i;
            const input = document.createElement("input");
            input.type = "number";
            input.id = inputId;
            input.className = "dynamicInput form-control form-control-lg form-control-borderless";

            const label = document.createElement("label");
            label.for = inputId;
            label.textContent = "y"+i;
            label.className = "form-check-label";

            div.appendChild(label);
            div.appendChild(input);

            const parent = document.getElementById("formArea");
            parent.appendChild(div);
        }
    }
}

function removeElementsByClass(className){
    const elements = document.getElementsByClassName(className);
    while(elements.length > 0){
        elements[0].parentNode.removeChild(elements[0]);
    }
}

