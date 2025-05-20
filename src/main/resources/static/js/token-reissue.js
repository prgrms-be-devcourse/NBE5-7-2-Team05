// src/utils/apiFetch.js
let refreshInFlight = null;

export async function apiFetch(url, options = {}, retry = true) {
    const cfg = { credentials: "include", ...options };
    const resp = await fetch(url, cfg);

    if (resp.status !== 401 || !retry) return resp;

    if (!refreshInFlight) {
        refreshInFlight = fetch("/users/reissue", {
            method: "POST",
            credentials: "include",
        }).then(r => {
            if (!r.ok) throw new Error("Refresh failed");
            return r;
        }).finally(() => {
            refreshInFlight = null;
        });
    }

    try {
        await refreshInFlight;
    } catch (_) {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        window.location.replace("/loginPage");
        throw _;
    }

    return fetch(url, cfg);
}
