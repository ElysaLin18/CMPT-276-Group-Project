// Quotable API by @lukePeavey
// https://github.com/lukePeavey/quotable

async function fetchQuote() {
    try {
        const response = await fetch('https://api.quotable.io/random?tags=religion%7Cgratitude%7Cgenerosity%7Chappiness%7Cinspirational%7Cvirtue%7Ccharacter%7Cfamily');
        const data = await response.json();
        console.log(data);
        return data;
    } catch (error) {
        console.error('Error fetching quote:', error);
        return null;
    }
}

async function updateQuote() {
    const { content, author } = await fetchQuote();
    if (content && author) {
        document.getElementById('quote').textContent = `" ${content} "`;
        document.getElementById('author').textContent = `- ${author}`;
    } else {
        document.getElementById('quote').textContent = 'Failed to fetch quote';
    }
}

updateQuote();

