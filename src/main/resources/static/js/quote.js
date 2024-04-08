// Function to fetch a random quote from the API
async function fetchQuote() {
    try {
        const response = await fetch('https://api.quotable.io/random?tags=religion%7Cgratitude%7Cgenerosity%7Chappiness%7Cinspirational%7Cvirtue%7Ccharacter%7Cfamily');
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching quote:', error);
        return null;
    }
}

// Function to update the HTML content with the fetched quote
async function updateQuote() {
    const { content, author } = await fetchQuote();
    if (content && author) {
        document.getElementById('quote').textContent = `"${content}"`;
        document.getElementById('author').textContent = `- ${author}`;
    } else {
        document.getElementById('quote').textContent = 'Failed to fetch quote';
    }
}

// Call updateQuote when the page loads
updateQuote();