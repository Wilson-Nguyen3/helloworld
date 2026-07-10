document.addEventListener('DOMContentLoaded', () => {
    const studentForm = document.getElementById('student-form');
    const studentNameInput = document.getElementById('student-name');
    const studentIdInput = document.getElementById('student-id');
    const studentDobInput = document.getElementById('student-dob');
    const messageArea = document.getElementById('message-area');
    const rosterCountSpan = document.getElementById('roster-count');

    // Auto-format DOB input as MM/DD/YY or MM/DD/YYYY
    studentDobInput.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, ''); // Keep only numbers
        let formatted = '';

        if (value.length > 0) {
            formatted = value.substring(0, 2);
            if (value.length > 2) {
                formatted += '/' + value.substring(2, 4);
                if (value.length > 4) {
                    formatted += '/' + value.substring(4, 8); // Limits year to 4 digits
                }
            }
        }
        e.target.value = formatted;
    });

    // Helper to convert MM/DD/YY or MM/DD/YYYY to YYYY-MM-DD
    function parseDateToIso(dateStr) {
        const parts = dateStr.split('/');
        if (parts.length !== 3) return null;

        const month = parts[0];
        const day = parts[1];
        let year = parts[2];

        // Basic validation for month and day ranges
        const m = parseInt(month, 10);
        const d = parseInt(day, 10);
        if (isNaN(m) || m < 1 || m > 12 || isNaN(d) || d < 1 || d > 31) {
            return null;
        }

        // Expand 2-digit years
        if (year.length === 2) {
            const currentYear = new Date().getFullYear();
            const currentCentury = Math.floor(currentYear / 100) * 100;
            const yearTwoDigit = currentYear % 100;
            const yrInt = parseInt(year, 10);

            // If the entered year is greater than current 2-digit year + 5, assume it is in the 1900s
            if (yrInt > yearTwoDigit + 5) {
                year = String(currentCentury - 100 + yrInt);
            } else {
                year = String(currentCentury + yrInt);
            }
        } else if (year.length !== 4) {
            return null; // Invalid year format
        }

        return `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
    }

    // Fetch and update total roster count
    async function updateRosterCount() {
        try {
            const response = await fetch('/students');
            if (response.ok) {
                const students = await response.json();
                rosterCountSpan.textContent = students.length;
            }
        } catch (error) {
            console.error('Error fetching roster count:', error);
        }
    }

    // Handle form submission
    studentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        // Hide message area
        messageArea.className = 'message-container';
        messageArea.style.display = 'none';

        const name = studentNameInput.value.trim();
        const id = studentIdInput.value.trim();
        const rawDob = studentDobInput.value.trim();

        const formattedDob = parseDateToIso(rawDob);
        if (!formattedDob) {
            showMessage('Error: Please enter a valid Date of Birth in MM/DD/YY or MM/DD/YYYY format.', 'error');
            return;
        }

        try {
            // Send request using application/x-www-form-urlencoded
            const params = new URLSearchParams();
            params.append('name', name);
            params.append('id', id);
            params.append('dob', formattedDob);

            const response = await fetch('/student', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: params
            });

            const resultText = await response.text();

            if (response.ok && !resultText.startsWith('Error:')) {
                showMessage(resultText, 'success');
                studentForm.reset();
                updateRosterCount();
            } else {
                showMessage(resultText, 'error');
            }
        } catch (error) {
            showMessage('Error: Failed to connect to server. Please try again.', 'error');
            console.error('Submission error:', error);
        }
    });

    function showMessage(text, type) {
        messageArea.textContent = text;
        messageArea.className = `message-container ${type}`;
        messageArea.style.display = 'block';
    }

    // Initial load
    updateRosterCount();
});
