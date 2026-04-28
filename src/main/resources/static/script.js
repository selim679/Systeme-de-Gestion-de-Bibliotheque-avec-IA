document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/books')
        .then(response => response.json())
        .then(books => {
            const booksList = document.getElementById('books-list');
            books.forEach(book => {
                const div = document.createElement('div');
                div.innerHTML = `<h2>${book.titre}</h2><p>Genre: ${book.genre}</p><p>Disponibles: ${book.disponibles}</p>`;
                booksList.appendChild(div);
            });
        })
        .catch(error => console.error('Erreur lors de la récupération des livres:', error));
});