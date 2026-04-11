document.addEventListener("DOMContentLoaded", function () {
    const typeSelect = document.getElementById("discountType");
    const valueInput = document.getElementById("discountValue");
    const valueUnit = document.getElementById("valueUnit");
    const valueUnitText = document.getElementById("valueUnitText");

    function updateUI() {
        const type = typeSelect.value;

        if (type === "PERCENTAGE") {
            valueUnit.innerText = "%";
            valueUnitText.innerText = "%";

            valueInput.step = "any";
            valueInput.min = 1;
            valueInput.max = 100;

        } else {
            valueUnit.innerText = "VND";
            valueUnitText.innerText = "₫";

            valueInput.step = "any";
            valueInput.min = 1000;
            valueInput.removeAttribute("max");
        }
    }

    typeSelect.addEventListener("change", updateUI);

    valueInput.addEventListener("input", () => {
        const type = typeSelect.value;
        const value = parseFloat(valueInput.value);

        if (type === "PERCENTAGE" && value > 1000) {
            valueInput.classList.add("is-invalid");
        } else {
            valueInput.classList.remove("is-invalid");
        }
    });

    document.querySelector("form").addEventListener("submit", function (e) {
        const type = typeSelect.value;
        const value = parseFloat(valueInput.value);

        if (type === "PERCENTAGE" && value > 100) {
            alert("Giảm giá % không được > 100%");
            e.preventDefault();
        }

        if (type === "FIXED" && value < 1000) {
            alert("Giảm giá tiền phải >= 1.000 VND");
            e.preventDefault();
        }
    });

    valueInput.addEventListener("input", () => {
        const type = typeSelect.value;
        const value = parseFloat(valueInput.value);

        if (type === "PERCENTAGE" && value > 1000) {
            alert("Bạn đang nhập tiền nhưng chọn % 😅");
        }
    });

    updateUI();
});