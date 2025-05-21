window.addEventListener("load", async function () {
    try {
        const res = await fetch('/users/me', { credentials: "include" });
        if (!res.ok) throw new Error("인증 필요");
        const response = await res.json();
        localStorage.setItem("userId", response.data);
        console.log("✅ userId 저장됨:", response.data);
    } catch (e) {
        console.error("로그인 필요:", e);
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
    }
}); 