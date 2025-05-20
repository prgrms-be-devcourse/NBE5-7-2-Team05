window.addEventListener("load", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const userIdFromURL = urlParams.get("userId");

    const existingUserId = localStorage.getItem("userId");

    if (!existingUserId && userIdFromURL) {
        localStorage.setItem("userId", userIdFromURL);
        console.log("✅ userId 저장됨:", userIdFromURL);
    } else {
        console.log("ℹ️ userId 저장 스킵 (이미 있음):", existingUserId);
    }
});
